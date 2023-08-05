package com.rodev.generator.action.entity.extra_data

import com.rodev.generator.action.entity.SelectorType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("selector")
@Serializable
data class SelectorExtraData(
    val selectorType: SelectorType
) : ExtraData()
