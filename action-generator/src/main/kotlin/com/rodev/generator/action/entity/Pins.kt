package com.rodev.generator.action.entity

object Pins {
    fun execPin(id: String, name: String = " ") = PinModel(
        id = id,
        type = "exec",
        label = name,
    )

    fun predicatePin(id: String, name: String = "Condition") = PinModel(
        id = id,
        type = "condition",
        label = name
    )
}
