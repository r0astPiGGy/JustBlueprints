package com.rodev.jbpkmp.domain.model.graph

import com.rodev.jbpkmp.domain.model.variable.LocalVariable
import com.rodev.jbpkmp.util.generateUniqueId
import com.rodev.nodeui.model.Graph
import kotlinx.serialization.Serializable

@Serializable
sealed interface GraphEntity {
    val id: String
    val graph: Graph
    val variables: List<String>
}

@Serializable
data class EventGraph(
    override val id: String = generateUniqueId(),
    override val graph: Graph,
    override val variables: List<String>
) : GraphEntity

@Serializable
data class FunctionGraph(
    val name: String,
    override val id: String = generateUniqueId(),
    override val graph: Graph,
    override val variables: List<String>
) : GraphEntity

@Serializable
sealed interface LegacyGraphEntity {

    val graph: Graph
    val variables: List<LocalVariable>
    val id: String

}