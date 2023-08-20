package com.rodev.jbpkmp.domain.model

import com.rodev.jbpkmp.domain.model.graph.*
import com.rodev.jbpkmp.domain.model.variable.GlobalVariable
import com.rodev.jbpkmp.domain.model.variable.LocalVariable
import com.rodev.jbpkmp.presentation.localization.processes
import com.rodev.jbpkmp.util.generateUniqueId
import com.rodev.nodeui.model.Graph
import kotlinx.serialization.Serializable

@Serializable
data class Blueprint(
    val eventGraph: EventGraph,
    val localVariables: List<LocalVariable>,
    val globalVariables: List<GlobalVariable>,
    val processes: List<FunctionGraph>,
    val functions: List<FunctionGraph>
)

@Serializable
data class LegacyBlueprint(
    val eventGraph: LegacyEventGraph,
    val processes: List<LegacyFunctionGraph>,
    val functions: List<LegacyFunctionGraph>
)

fun LegacyBlueprint.toBlueprint(): Blueprint {
    return fromLegacyBlueprint(this)
}

fun fromLegacyBlueprint(blueprint: LegacyBlueprint): Blueprint {
    val localVariables = mutableListOf<LocalVariable>()
    val globalVariables = blueprint.eventGraph.globalVariables

    mutableListOf<LegacyGraphEntity>(
        blueprint.eventGraph
    ).apply {
        addAll(blueprint.functions)
        addAll(blueprint.processes)
    }.forEach {
        localVariables.addAll(it.variables)
    }

    return Blueprint(
        eventGraph = EventGraph(
            id = blueprint.eventGraph.id,
            graph = blueprint.eventGraph.graph,
            variables = blueprint.eventGraph.localVariables.map { it.id }
        ),
        localVariables = localVariables,
        globalVariables = globalVariables,
        processes = blueprint.processes.map { process ->
            FunctionGraph(
                id = process.id,
                name = process.name,
                graph = process.graph,
                variables = process.variables.map { it.id }
            )
        },
        functions = blueprint.functions.map { function ->
            FunctionGraph(
                id = function.id,
                name = function.name,
                graph = function.graph,
                variables = function.variables.map { it.id }
            )
        },
    )
}

fun emptyBlueprint(): Blueprint {
    return Blueprint(
        eventGraph = EventGraph(
            id = generateUniqueId(),
            variables = emptyList(),
            graph = Graph(
                emptyList(),
                emptyList()
            )
        ),
        localVariables = emptyList(),
        globalVariables = emptyList(),
        processes = emptyList(),
        functions = emptyList()
    )
}