package com.rodev.jbpkmp.data

data class Graph(
    val connections: List<PinConnection>,
    val nodes: List<Node>,
)