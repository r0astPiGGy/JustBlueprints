package com.rodev.nodeui.model

import kotlinx.serialization.Serializable

@Serializable
data class PinConnection(
    val inputPinId: String,
    val outputPinId: String
)