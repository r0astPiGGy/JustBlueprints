package com.rodev.nodeui.model

import kotlinx.serialization.Serializable

@Serializable
data class Graph(
    val connections: List<PinConnection>,
    val nodes: List<Node>,
)