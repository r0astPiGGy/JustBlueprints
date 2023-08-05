package com.rodev.generator.action.entity

import com.rodev.generator.action.entity.extra_data.ConnectionDisabledExtraData
import com.rodev.generator.action.entity.extra_data.ExecPairExtraData

object Pins {
    fun execPin(id: String, name: String = " ") = PinModel(
        id = id,
        type = "exec",
        label = name,
        extra = ExecPairExtraData
    )

    fun predicatePin(id: String, name: String = "Условие") = PinModel(
        id = id,
        type = "condition",
        label = name
    )

    fun selectorPin(selectorType: SelectorType) = PinModel(
        id = selectorType.id + "_selector",
        type = selectorType.id,
        label = "Селектор",
        extra = ConnectionDisabledExtraData
    )
}
