package com.rodev.jbpkmp.domain.model

import com.rodev.jbpkmp.domain.model.graph.EventGraph
import com.rodev.jbpkmp.domain.model.graph.FunctionGraph
import com.rodev.nodeui.model.Graph
import kotlinx.serialization.Serializable

@Serializable
data class Blueprint(
    val eventGraph: EventGraph,
    val processes: List<FunctionGraph>,
    val functions: List<FunctionGraph>
)

fun emptyBlueprint(): Blueprint {
    return Blueprint(
        eventGraph = EventGraph(
            emptyList(),
            globalVariables = emptyList(),
            graph = Graph(
                emptyList(),
                emptyList()
            )
        ),
        processes = emptyList(),
        functions = emptyList()
    )
}