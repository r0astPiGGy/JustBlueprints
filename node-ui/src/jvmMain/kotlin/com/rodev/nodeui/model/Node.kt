package com.rodev.nodeui.model

import kotlinx.serialization.Serializable

@Serializable
data class Node(
    val x: Float,
    val y: Float,
    val uniqueId: String,
    val typeId: String,
    val inputPins: List<Pin>,
    val outputPins: List<Pin>
)