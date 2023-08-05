package com.rodev.generator.action.entity

import com.rodev.generator.action.entity.extra_data.ConnectionDisabledExtraData
import com.rodev.generator.action.entity.extra_data.ExecPairExtraData
import com.rodev.generator.action.entity.extra_data.SelectorExtraData
import com.rodev.generator.action.entity.extra_data.buildCompoundExtraData

object Pins {
    fun execPin(id: String, name: String = " ") = PinModel(
        id = id,
        type = "exec",
        label = name,
        extra = ExecPairExtraData
    )

    fun predicatePin(id: String, name: String = "Condition") = PinModel(
        id = id,
        type = "condition",
        label = name
    )

    fun selectorPin(selectorType: SelectorType) = PinModel(
        id = selectorType.id,
        type = selectorType.id,
        label = selectorType.name,
        extra = buildCompoundExtraData {
            add(ConnectionDisabledExtraData)
            add(SelectorExtraData(selectorType))
        }
    )
}
