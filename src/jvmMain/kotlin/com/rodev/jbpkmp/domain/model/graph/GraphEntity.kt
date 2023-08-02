package com.rodev.jbpkmp.domain.model.graph

import com.rodev.jbpkmp.domain.model.saveable_variable.scope.VariableScope
import com.rodev.nodeui.model.Graph
import kotlinx.serialization.Serializable

@Serializable
sealed interface GraphEntity : VariableScope {

    val graph: Graph

}