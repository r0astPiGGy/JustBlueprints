package com.rodev.jbpkmp.domain.model

import com.rodev.jbpkmp.domain.model.graph.EventGraph
import com.rodev.jbpkmp.domain.model.graph.FunctionGraph
import kotlinx.serialization.Serializable

@Serializable
data class Blueprint(
    val eventGraph: EventGraph,
    val processes: List<FunctionGraph>,
    val functions: List<FunctionGraph>
)