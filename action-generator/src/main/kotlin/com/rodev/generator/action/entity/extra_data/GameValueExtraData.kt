package com.rodev.generator.action.entity.extra_data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("game_value")
data class GameValueExtraData(val id: String) : ExtraData()