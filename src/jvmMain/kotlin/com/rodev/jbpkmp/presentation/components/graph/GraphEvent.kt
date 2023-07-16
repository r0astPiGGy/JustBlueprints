package com.rodev.jbpkmp.presentation.components.graph

import com.rodev.jbpkmp.data.Node
import java.util.*

sealed interface GraphEvent

data class NodeAddEvent(
    val node: Node
) : GraphEvent

object NodeClearEvent : GraphEvent

data class NodeDeleteEvent(val node: UUID): GraphEvent