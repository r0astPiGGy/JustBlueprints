package com.rodev.nodeui.components.graph

import com.rodev.nodeui.model.Node
import java.util.*

interface GraphEvent

data class NodeAddEvent(
    val node: Node
) : GraphEvent

object NodeClearEvent : GraphEvent