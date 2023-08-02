package com.rodev.jbpkmp.domain.model.graph

import com.rodev.jbpkmp.domain.model.saveable_variable.LocalVariable
import com.rodev.nodeui.model.Graph
import kotlinx.serialization.Serializable

@Serializable
data class FunctionGraph(
    val functionId: String,
    override val variables: List<LocalVariable>,
    override val graph: Graph
): GraphEntity
