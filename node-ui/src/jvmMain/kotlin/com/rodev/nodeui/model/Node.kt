package com.rodev.nodeui.model

import com.rodev.nodeui.model.tag.MapTag
import kotlinx.serialization.Serializable

@Serializable
data class Node(
    val x: Float,
    val y: Float,
    val uniqueId: String,
    val inputPins: List<Pin>,
    val outputPins: List<Pin>,
    val tag: MapTag = MapTag()
)