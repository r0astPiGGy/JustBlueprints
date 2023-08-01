package com.rodev.generator.action.interpreter.pin_type

import com.rodev.generator.action.entity.NodeModel
import com.rodev.generator.action.entity.PinModel
import com.rodev.generator.action.entity.PinType

class PinTypeInterpreter(
    private val nodes: List<NodeModel>
) {

    fun interpret(): List<PinType> {
        val types = hashSetOf<String>()

        fun List<PinModel>.extractTypes() {
            forEach {
                types.add(it.type)
            }
        }

        nodes.forEach {
            it.input.extractTypes()
            it.output.extractTypes()
        }

        return types.map { PinType(id = it, color = 0) }
    }

}