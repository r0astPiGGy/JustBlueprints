package com.rodev.jbpkmp.data

data class NodeEntity(
    val id: String,
    val header: String,
    val headerColor: Int,
    val x: Float,
    val y: Float,
    val inputPins: List<PinEntity>,
    val outputPins: List<PinEntity>
)
