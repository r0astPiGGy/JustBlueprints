package com.rodev.jbpkmp.domain.model.graph

import com.rodev.jbpkmp.domain.model.variable.LocalVariable
import com.rodev.nodeui.model.Graph
import kotlinx.serialization.Serializable

@Serializable
data class FunctionGraph(
    val functionId: String,
    override val variables: List<LocalVariable>,
    override val graph: Graph
) : GraphEntity
