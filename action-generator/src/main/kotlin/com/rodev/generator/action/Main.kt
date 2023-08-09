package com.rodev.generator.action

import com.rodev.generator.action.entity.*
import com.rodev.generator.action.entity.Pins.containerExecPin
import com.rodev.generator.action.entity.Pins.execPin
import com.rodev.generator.action.entity.Pins.invertConditionPin
import com.rodev.generator.action.entity.Pins.outputExecPin
import com.rodev.generator.action.entity.Pins.predicatePin
import com.rodev.generator.action.entity.Pins.selectorPin
import com.rodev.generator.action.entity.extra_data.*
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
import com.rodev.generator.action.utils.toMap
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
import kotlin.math.abs
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

    val actionDetails = nodeCompounds.map { it.toActionDetails() }

    BulkWriter.bulkWrite {
        file("actions.json") { writeJson(interpretedActions) }
        file("categories.json") { writeJson(categories) }
        file("node-models.json") { writeJson(interpretedNodeModels) }
        file("pin-types.json") { writeJson(pinTypes) }
        file("node-types.json") { writeJson(nodeTypes) }
        file("action-details.json") { writeJson(actionDetails) }

        folder("src/jvmMain/resources/data")
        folder("action-generator-output") {
            file("log.txt") { ActionLogger.writeTo(this) }
            file("absent-actions.txt") { writeJson(compareData(extractedActions, extractedRawActions)) }
        }
    }
}

fun compareData(actions: List<ActionData>, rawActions: List<RawActionData>): List<String> {
    val rawMapped = rawActions.toMap(RawActionData::id)
    val actionsMapped = actions.toMap(ActionData::id)

    val absentActions = mutableListOf<String>()

    actionsMapped.keys.forEach {
        if (rawMapped[it] == null) {
            absentActions += it
        }
    }

    return absentActions
}

private fun createPinInterpreterRegistry(localeProvider: LocaleProvider) = PinInterpreterRegistry.build(localeProvider) {
    registerPinExtraDataProvider(type = "enum") { actionData, argument, _ ->
        val enums = argument.`enum`

        fun List<String>.toEnumExtraData(): EnumExtraData {
            return EnumExtraData(map {
                EnumEntry(id = it, localeProvider.translateEnumName(actionData, argument, it))
            })
        }

        if (enums == null) {
            ActionLogger.log("Enum is null for argument ${argument.name} in action ${actionData.id}")
            return@registerPinExtraDataProvider null
        }

        return@registerPinExtraDataProvider buildCompoundExtraData {
            add(enums.toEnumExtraData())
            add(ConnectionDisabledExtraData)
        }
    }
    registerPinExtraDataProvider(type = "boolean") { _, _, _ ->
        return@registerPinExtraDataProvider ConnectionDisabledExtraData
    }
}

@Deprecated("Deprecated", ReplaceWith("this"))
fun String.anyToDynamic() = this

private fun createNodeInterpreterPipeline(
    pinInterpreterRegistry: PinInterpreterRegistry,
    localeProvider: LocaleProvider,
    categoryResolver: ActionCategoryResolver
) = NodeInterpreterPipeline.build(pinInterpreterRegistry, DefaultNodeInterpreter(localeProvider, categoryResolver)) {
    fun List<PinModel>.addFirst(vararg pinModels: PinModel): List<PinModel> {
        return pinModels.toMutableList().also { it.addAll(this) }
    }

    pipeline {
        return@pipeline it.copy(
            input = action.args.map(::interpretPin)
        )
    }.add {
        if (action.containing == "predicate") {
            return@add it.copy(
                type = "pure_function",
                output = listOf(predicatePin("return_value"))
            )
        }
        return@add it
    }.add {
        SelectorType.fromId(action.`object`)?.let { selector ->
            return@add it.copy(
                input = it.input.toMutableList().also { list -> list.add(0, selectorPin(selector)) }
            )
        }
        return@add it
    }.add {
        val extra = when {
            action.containing == "predicate" -> ConditionalExtraData
            rawAction.type == "container" -> ContainerExtraData
            else -> null
        }

        return@add it.copy(
            extra = extra
        )
    }.add{
        if (it.extra is ContainerExtraData) {
            return@add it.copy(
                output = it.output.addFirst(containerExecPin("Тело"))
            )
        }
        if (rawAction.type == "container_with_conditional") {
            return@add it.copy(
                input = it.input.addFirst(
                    predicatePin("condition"),
                    invertConditionPin("invert_condition")
                ),
                output = it.output.addFirst(containerExecPin("Тело")),
                extra = if (it.extra == null) ContainerExtraData else buildCompoundExtraData {
                    add(ContainerExtraData)
                    add(it.extra)
                }
            )
        }
        if (rawAction.type == "basic_with_conditional") {
            return@add it.copy(
                input = it.input.addFirst(predicatePin("condition"))
            )
        }

        return@add it
    }.add {
        if (it.type == "function") {
            return@add it.copy(
                input = it.input.addFirst(execPin("in-exec")),
                output = it.output.addFirst(outputExecPin("out-exec"))
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
        val details = ActionDetails(
            id = action.id,
            name = localeProvider.translateActionName(action),
            description = localeProvider.translateActionDescription(action),
            additionalInfo = localeProvider.translateActionAdditionalInformation(rawActionData),
            worksWith = localeProvider.translateActionWorksWith(rawActionData)
        )

        return NodeCompound(
            id = action.id,
            type = "function",
            name = localeProvider.translateActionName(action),
            input = emptyList(),
            output = emptyList(),
            iconPath = iconPathFrom("actions", action.id),
            category = categoryResolver.resolveCategoryFor(action) ?: "no-category",
            details = details
        )
    }
}

inline fun <reified T> File.writeJson(data: T) {
    writeText(json.encodeToString(data))
}