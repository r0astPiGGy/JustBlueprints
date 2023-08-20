package com.rodev.jbpkmp.domain.model.graph

import com.rodev.jbpkmp.domain.model.variable.GlobalVariable
import com.rodev.jbpkmp.domain.model.variable.LocalVariable
import com.rodev.jbpkmp.util.generateUniqueId
import com.rodev.nodeui.model.Graph
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("event-graph")
data class LegacyEventGraph(
    val localVariables: List<LocalVariable>,
    val globalVariables: List<GlobalVariable>,
    override val graph: Graph,
    override val id: String = generateUniqueId()
) : LegacyGraphEntity {

    override val variables
        get() = localVariables

}
