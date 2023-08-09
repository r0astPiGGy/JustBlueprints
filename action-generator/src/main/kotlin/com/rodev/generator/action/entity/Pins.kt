package com.rodev.generator.action.entity

import com.rodev.generator.action.entity.Pins.Type.BOOLEAN
import com.rodev.generator.action.entity.Pins.Type.CONDITION
import com.rodev.generator.action.entity.Pins.Type.EXECUTION
import com.rodev.generator.action.entity.extra_data.*

object Pins {

    object Type {
        const val CONDITION = "condition"
        const val EXECUTION = "exec"
        const val BOOLEAN = "boolean"

        val SELECTOR = SelectorType.values().map { it.id }.toSet()
    }

    fun outputExecPin(id: String, name: String = " ") = PinModel(
        id = id,
        type = EXECUTION,
        label = name,
        extra = buildCompoundExtraData {
            add(ExecPairExtraData)
            add(ExecNextExtraData)
        }
    )

    fun execPin(id: String, name: String = " ") = PinModel(
        id = id,
        type = EXECUTION,
        label = name,
        extra = ExecPairExtraData
    )

    fun containerExecPin(name: String) = PinModel(
        id = "container-exec",
        type = EXECUTION,
        label = name,
        extra = ExecContainerExtraData
    )

    fun predicatePin(id: String, name: String = "Условие") = PinModel(
        id = id,
        type = CONDITION,
        label = name
    )

    fun invertConditionPin(id: String, name: String = "Инвертировать") = PinModel(
        id = id,
        type = BOOLEAN,
        label = name,
        extra = buildCompoundExtraData {
            add(InvertConditionExtraData)
            add(ConnectionDisabledExtraData)
        }
    )

    fun selectorPin(selectorType: SelectorType) = PinModel(
        id = selectorType.id + "_selector",
        type = selectorType.id,
        label = "Селектор",
        extra = ConnectionDisabledExtraData
    )
}
