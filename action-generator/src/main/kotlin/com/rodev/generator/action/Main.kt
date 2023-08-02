package com.rodev.generator.action

import com.rodev.generator.action.entity.*
import com.rodev.generator.action.entity.Pins.execPin
import com.rodev.generator.action.entity.Pins.predicatePin
import com.rodev.generator.action.entity.extra_data.DictionaryExtraData
import com.rodev.generator.action.entity.extra_data.EnumEntry
import com.rodev.generator.action.entity.extra_data.EnumExtraData
import com.rodev.generator.action.entity.extra_data.ListExtraData
import com.rodev.generator.action.interpreter.BulkNodeCompoundInterpreter
import com.rodev.generator.action.interpreter.action.ActionDataInterpreter
import com.rodev.generator.action.interpreter.action.NodeInterpreter
import com.rodev.generator.action.interpreter.action.NodeInterpreterPipeline
import com.rodev.generator.action.interpreter.action.PinInterpreterRegistry
import com.rodev.generator.action.interpreter.category.ActionCategoryResolver
import com.rodev.generator.action.interpreter.category.CategoryInterpreter
import com.rodev.generator.action.interpreter.event.EventDataMapper
import com.rodev.generator.action.interpreter.event.EventInterpreter
import com.rodev.generator.action.interpreter.game_value.GameValueInterpreter
import com.rodev.generator.action.interpreter.node_type.NodeTypeInterpreter
import com.rodev.generator.action.interpreter.pin_type.PinTypeInterpreter
import com.rodev.generator.action.patch.applyPatcher
import com.rodev.generator.action.patch.entity.*
import com.rodev.generator.action.utils.Resources
import com.rodev.generator.action.writer.BulkWriter
import com.rodev.jmcc_extractor.*
import com.rodev.jmcc_extractor.entity.ActionData
import com.rodev.jmcc_extractor.entity.RawActionData
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

val json = Json {
    prettyPrint = true
}

@OptIn(ExperimentalTime::class)
suspend fun <T> runMeasuring(tag: String = "", scope: suspend () -> T): T {
    val value = measureTimedValue {
        scope()
    }

    println("Took ${value.duration.inWholeMilliseconds}ms. to load $tag")

    return value.value
}

fun <T> runAsync(block: suspend CoroutineScope.() -> T): Deferred<T> {
    return CoroutineScope(Dispatchers.Default).async(block = block)
}

@OptIn(ExperimentalSerializationApi::class)
fun main() = runBlocking {
    // TODO: implement cache
    val extractedActionsJob = runAsync {
        runMeasuring(tag = "ActionData") {
            ActionDataExtractor.load()
        }
    }
    val extractedRawActionsJob = runAsync {
        runMeasuring(tag = "RawActionData") {
            RawActionDataExtractor.load()
        }
    }
    val localeDataSourceJob = runAsync {
        runMeasuring(tag = "LocaleData") {
            LocaleExtractor.load().toLocaleDataSource()
        }
    }
    val eventDataSourceJob = runAsync {
        runMeasuring(tag = "EventData") {
            EventDataExtractor.load()
        }
    }
    val gameValueDataSourceJob = runAsync {
        runMeasuring(tag = "GameValueData") {
            GameValueDataExtractor.load()
        }
    }
    val customNodesJob = runAsync {
        runMeasuring(tag = "CustomNodes") {
            Json.decodeFromStream<List<NodeCompound>>(Resources.loadResource("custom-nodes.json"))
        }
    }

    val extractedActions = extractedActionsJob.await()
    val extractedRawActions = extractedRawActionsJob.await()
    val localeDataSource = localeDataSourceJob.await()
    val extractedEventData = eventDataSourceJob.await()
    val extractedGameValues = gameValueDataSourceJob.await()
    val customNodes = customNodesJob.await()

    val localeProvider = LocaleProvider(localeDataSource)
    val actionCategoryResolver = ActionCategoryResolver(extractedRawActions)

    val pinInterpreterRegistry = createPinInterpreterRegistry(localeProvider)
    val nodeInterpreterPipeline = createNodeInterpreterPipeline(
        pinInterpreterRegistry,
        localeProvider,
        actionCategoryResolver
    )

    val eventDataMapper = EventDataMapper(
        extractedEventData,
        extractedGameValues
    )

    val bulkInterpreter = BulkNodeCompoundInterpreter.build {
        addInterpreter {
            ActionDataInterpreter(nodeInterpreterPipeline)
                .interpret(extractedActions, extractedRawActions)
        }
        addInterpreter {
            EventInterpreter(localeProvider, eventDataMapper).interpret(extractedEventData)
        }
        addInterpreter {
            GameValueInterpreter(localeProvider).interpret(extractedGameValues)
        }
        addInterpreter { customNodes }
    }

    val nodeCompounds = bulkInterpreter.interpret()

    val interpretedNodeModels = nodeCompounds.map { it.toNodeModel() }
        .applyPatcher<NodeModelPatch, NodeModel>(
            patchesListResource = "patches/node-models.json",
            idExtractor = NodeModel::id,
            patchFunction = ::patchNodeModel
        )

    val interpretedActions = nodeCompounds.map { it.toAction() }
        .applyPatcher<ActionPatch, Action>(
            patchesListResource = "patches/actions.json",
            idExtractor = Action::id,
            patchFunction = ::patchAction
        )

    val categories = CategoryInterpreter(localeProvider)
        .interpret(interpretedActions)
        .applyPatcher<CategoryPatch, Category>(
            patchesListResource = "patches/categories.json",
            idExtractor = Category::path,
            patchFunction = ::patchCategory
        )

    val pinTypes = PinTypeInterpreter(interpretedNodeModels)
        .interpret()
        .applyPatcher<PinTypePatch, PinType>(
            patchesListResource = "patches/pin-types.json",
            idExtractor = PinType::id,
            patchFunction = ::patchPinType
        )

    val nodeTypes = NodeTypeInterpreter(interpretedNodeModels)
        .interpret()
        .applyPatcher<NodeTypePatch, NodeType>(
            patchesListResource = "patches/node-types.json",
            idExtractor = NodeType::id,
            patchFunction = ::patchNodeType
        )

    BulkWriter.bulkWrite {
        file("actions.json") { writeJson(interpretedActions) }
        file("categories.json") { writeJson(categories) }
        file("node-models.json") { writeJson(interpretedNodeModels) }
        file("pin-types.json") { writeJson(pinTypes) }
        file("node-types.json") { writeJson(nodeTypes) }

        folder("src/jvmMain/resources/data")
        folder("action-generator-output") {
            file("action-namespaces.txt") { extractedActions.writeNamespacesTo(this) }
            file("log.txt") { ActionLogger.writeTo(this) }
        }
    }
}

private fun List<ActionData>.writeNamespacesTo(file: File) {
    val map = hashMapOf<String, Int>()

    forEach {
        val id = it.id.replace(it.name, "")
        map.compute(id) { _, v -> (v ?: 0) + 1 }
    }

    val list = map.map { it }.sortedWith { o1, o2 ->
        compareValues(o1.value, o2.value)
    }

    file.writeText(list.joinToString(separator = "\n") { "${it.key} : ${it.value}" })
}

private fun createPinInterpreterRegistry(localeProvider: LocaleProvider) = PinInterpreterRegistry.build(localeProvider) {
    registerPinExtraDataProvider(type = "enum") { actionData, argument, _ ->
        val enums = argument.`enum`

        if (enums == null) {
            ActionLogger.log("Enum is null for argument ${argument.name} in action ${actionData.id}")
            return@registerPinExtraDataProvider null
        }

        return@registerPinExtraDataProvider EnumExtraData(enums.map {
            EnumEntry(id = it, localeProvider.translateEnumName(actionData, argument, it))
        })
    }
    registerPinExtraDataProvider(type = "dictionary") { actionData, argument, rawArgument ->
        if (rawArgument?.keyType == null || rawArgument.valueType == null) {
            ActionLogger.log("Key type and value type are null for dictionary '${argument.name}' in action ${actionData.id}")
            return@registerPinExtraDataProvider null
        }

        return@registerPinExtraDataProvider DictionaryExtraData(
            keyType = rawArgument.keyType!!.anyToDynamic(),
            elementType = rawArgument.valueType!!.anyToDynamic()
        )
    }
    registerPinExtraDataProvider(type = "list") { actionData, argument, rawArgument ->
        if (rawArgument?.elementType == null) {
            ActionLogger.log("Element type is null for list '${argument.name}' in action ${actionData.id}")
            return@registerPinExtraDataProvider null
        }

        return@registerPinExtraDataProvider ListExtraData(
            elementType = rawArgument.elementType!!.anyToDynamic()
        )
    }
}

@Deprecated("Deprecated", ReplaceWith("this"))
fun String.anyToDynamic() = this

private fun createNodeInterpreterPipeline(
    pinInterpreterRegistry: PinInterpreterRegistry,
    localeProvider: LocaleProvider,
    categoryResolver: ActionCategoryResolver
) = NodeInterpreterPipeline.build(pinInterpreterRegistry, DefaultNodeInterpreter(localeProvider, categoryResolver)) {
    pipeline {
        return@pipeline it.copy(
            input = action.args.map(::interpretPin)
        )
//    }.add {
//        var type = it.type
//        if (action.id.startsWith("set_variable_get")) {
//            type = if (action.args[1].type.equals(action.origin) && action.args.size < 3) {
//                "variable_property"
//            } else {
//                "pure_function"
//            }
//        }
//
//        return@add it.copy(
//            type = type
//        )
//    }.add {
//        if (it.id.startsWith("set_variable")) {
//            val output = it.input[0]
//            val input = it.input.toMutableList().also { list -> list.removeAt(0) }
//
//            return@add it.copy(
//                input = input,
//                output = listOf(output)
//            )
//        }
//        return@add it
    }.add {
        if (action.containing == "predicate") {
            return@add it.copy(
                type = "pure_function",
                output = listOf(predicatePin("return_value"))
            )
        }
        return@add it
    }.add {
        if (it.type == "function") {
            fun List<PinModel>.addExec(id: String): List<PinModel> {
                return toMutableList().also { list -> list.add(0, execPin(id)) }
            }

            return@add it.copy(
                input = it.input.addExec("in-exec"),
                output = it.output.addExec("out-exec")
            )
        }
        return@add it
    }
}

class DefaultNodeInterpreter(
    private val localeProvider: LocaleProvider,
    private val categoryResolver: ActionCategoryResolver
) : NodeInterpreter {

    override fun interpret(action: ActionData, rawActionData: RawActionData): NodeCompound {
        return NodeCompound(
            id = action.id,
            type = "function",
            name = localeProvider.translateActionName(action),
            input = emptyList(),
            output = emptyList(),
            iconPath = iconPathFrom("actions", action.id),
            category = categoryResolver.resolveCategoryFor(action) ?: "no-category"
        )
    }
}

inline fun <reified T> File.writeJson(data: T) {
    writeText(json.encodeToString(data))
}