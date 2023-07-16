package com.rodev.jbpkmp.data

data class Node(
    val x: Float,
    val y: Float,
    val uniqueId: String,
    val typeId: String,
    val inputPins: List<Pin>,
    val outputPins: List<Pin>
)