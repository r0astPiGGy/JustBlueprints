package com.rodev.generator.action.entity.extra_data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("event")
@Serializable
data class EventExtraData(
    val cancellable: Boolean
) : ExtraData()