package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation

import com.rodev.generator.action.entity.PinType
import com.rodev.nodeui.components.graph.PinTypeComparator
import com.rodev.nodeui.components.pin.PinState

object DefaultPinTypeComparator : PinTypeComparator {

    private val exceptionalTypes = setOf("exec", "boolean", "enum", "condition")

    override fun connectable(inputPin: PinState, outputPin: PinState): Boolean {
        val inputType = inputPin.type
        val outputType = outputPin.type

        if (anyExceptional(inputType, outputType)) {
            return typeEquals(inputType, outputType)
        }

        return true
    }

    private fun anyExceptional(vararg types: PinType): Boolean {
        for (type in types) {
            if (exceptionalTypes.contains(type.id)) return true
        }
        return false
    }

    private fun typeEquals(type: PinType, vararg types: PinType): Boolean {
        types.forEach {
            if (it != type) return false
        }
        return true
    }
}

private val PinState.type
    get() = pinDisplay.type as PinType