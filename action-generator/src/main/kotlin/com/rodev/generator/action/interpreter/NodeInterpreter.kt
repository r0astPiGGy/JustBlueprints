package com.rodev.generator.action.interpreter

import com.rodev.generator.action.LocaleProvider
import com.rodev.generator.action.entity.NodeModel
import com.rodev.generator.action.entity.PinModel
import com.rodev.jmcc_extractor.entity.ActionData
import com.rodev.jmcc_extractor.entity.Argument

class NodeInterpreter(
    private val localeProvider: LocaleProvider
) : ListInterpreter<ActionData, NodeModel>() {

    override fun interpretElement(input: ActionData): NodeModel {
        return NodeModel(
            id = input.id,
            type = "function",
            input = getInputPins(input),
            output = emptyList()
        )
    }

    private fun getInputPins(actionData: ActionData): List<PinModel> {
        return actionData.args.map { inputPin(actionData, it) }
    }

    private fun inputPin(actionData: ActionData, argument: Argument): PinModel {
        return PinModel(
            id = argument.name.toString(),
            label = localeProvider.translateArgName(actionData, argument),
            type = argument.type.toString()
        )
    }

}