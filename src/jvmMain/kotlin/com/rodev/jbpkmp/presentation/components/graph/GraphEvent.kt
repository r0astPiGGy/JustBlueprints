package com.rodev.jbpkmp.presentation.components.graph

import com.rodev.jbpkmp.data.NodeEntity

sealed interface GraphEvent

data class NodeAddEvent(
    val nodeEntity: NodeEntity
) : GraphEvent

object NodeClearEvent : GraphEvent