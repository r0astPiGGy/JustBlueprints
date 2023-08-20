package com.rodev.jbpkmp.domain.model.graph

import com.rodev.jbpkmp.domain.model.variable.LocalVariable
import com.rodev.jbpkmp.util.generateUniqueId
import com.rodev.nodeui.model.Graph
import kotlinx.serialization.Serializable

@Serializable
data class LegacyFunctionGraph(
    val name: String,
    override val id: String = generateUniqueId(),
    override val variables: List<LocalVariable>,
    override val graph: Graph
) : LegacyGraphEntity
