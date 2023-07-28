package com.rodev.generator.action

import com.rodev.generator.action.interpreter.ActionCategoryResolver
import com.rodev.generator.action.interpreter.ActionInterpreter
import com.rodev.generator.action.interpreter.CategoryInterpreter
import com.rodev.generator.action.interpreter.NodeInterpreter
import com.rodev.generator.action.writer.BulkWriter
import com.rodev.jmcc_extractor.ActionDataExtractor
import com.rodev.jmcc_extractor.LocaleExtractor
import com.rodev.jmcc_extractor.RawActionDataExtractor
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

val json = Json {
    prettyPrint = true
}

@OptIn(ExperimentalTime::class)
fun <T> runMeasuring(tag: String = "", scope: () -> T): T {
    val value = measureTimedValue(scope)

    println("Took ${value.duration.inWholeMilliseconds}ms. to load $tag")

    return value.value
}

fun main() = runBlocking {
    val extractedActionsJob = CoroutineScope(Dispatchers.Default).async {
        runMeasuring(tag = "ActionData") {
            ActionDataExtractor.load()
        }
    }
    val extractedRawActionsJob = CoroutineScope(Dispatchers.Default).async {
        runMeasuring(tag = "RawActionData") {
            RawActionDataExtractor.load()
        }
    }
    val localeDataSourceJob = CoroutineScope(Dispatchers.Default).async {
        runMeasuring(tag = "LocaleData") {
            LocaleExtractor.load().toLocaleDataSource()
        }
    }

    val extractedActions = extractedActionsJob.await()
    val extractedRawActions = extractedRawActionsJob.await()
    val localeDataSource = localeDataSourceJob.await()

    val localeProvider = LocaleProvider(localeDataSource)
    val actionCategoryResolver = ActionCategoryResolver(extractedRawActions)

    val interpretedNodeModels = NodeInterpreter(localeProvider).interpret(extractedActions)
    val interpretedActions = ActionInterpreter(actionCategoryResolver, localeProvider).interpret(extractedActions)
    val categories = CategoryInterpreter(localeProvider).interpret(extractedRawActions)

    BulkWriter.bulkWrite {
        file("actions.json") { writeJson(interpretedActions) }
        file("categories.json") { writeJson(categories) }
        file("node-models.json") { writeJson(interpretedNodeModels) }

        folder("src/jvmMain/resources/data")
        folder("action-generator-output") {
            file("log.txt") { ActionLogger.writeTo(this) }
        }
    }
}

inline fun <reified T> File.writeJson(data: T) {
    writeText(json.encodeToString(data))
}