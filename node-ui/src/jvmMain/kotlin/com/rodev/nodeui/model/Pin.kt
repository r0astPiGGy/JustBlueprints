package com.rodev.nodeui.model

import com.rodev.nodeui.model.tag.MapTag
import kotlinx.serialization.Serializable

@Serializable
data class Pin(
    val uniqueId: String,
    val tag: MapTag = MapTag()
)
