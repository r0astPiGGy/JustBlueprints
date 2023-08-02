package com.rodev.jbpkmp.domain.model.graph

import com.rodev.jbpkmp.domain.model.variable.GlobalVariable
import com.rodev.jbpkmp.domain.model.variable.LocalVariable
import com.rodev.nodeui.model.Graph
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("event-graph")
data class EventGraph(
    val localVariables: List<LocalVariable>,
    val globalVariables: List<GlobalVariable>,
    override val graph: Graph
) : GraphEntity {

    override val variables
        get() = globalVariables

}
