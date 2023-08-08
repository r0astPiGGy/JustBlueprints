package com.rodev.generator.action.entity.extra_data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("handler")
data class HandlerExtraData(val id: String) : ExtraData()