package com.rodev.generator.action.interpreter.action

import com.rodev.generator.action.ActionLogger
import com.rodev.generator.action.entity.NodeCompound
import com.rodev.generator.action.interpreter.ActionInterpreter
import com.rodev.generator.action.utils.toMap
import com.rodev.jmcc_extractor.entity.ActionData
import com.rodev.jmcc_extractor.entity.RawActionData

class ActionDataInterpreter(
    private val nodeInterpreterPipeline: NodeInterpreterPipeline
) : ActionInterpreter<ActionCompound> {

    fun interpret(actions: List<ActionData>, rawActions: List<RawActionData>): List<NodeCompound> {
        return interpret(compound(actions, rawActions))
    }

    override fun interpret(list: List<ActionCompound>): List<NodeCompound> {
        return list.map { interpret(it.actionData, it.rawActionData) }
    }

    private fun interpret(action: ActionData, rawActionData: RawActionData): NodeCompound {
        return nodeInterpreterPipeline.interpret(action, rawActionData)
    }
}

fun compound(actions: List<ActionData>, rawActions: List<RawActionData>): List<ActionCompound> {
    val mappedActions = rawActions.toMap(RawActionData::id)

    return actions.map { action -> compoundOrInterpret(action, mappedActions[action.id]) }
}

private fun compoundOrInterpret(action: ActionData, rawAction: RawActionData?): ActionCompound {
    return ActionCompound(action, rawAction ?: interpret(action))
}

private fun interpret(action: ActionData): RawActionData {
    ActionLogger.log("RawActionData not found by id ${action.id}")

    val type = if (action.id.startsWith("if_")) {
        "container"
    } else {
        "basic"
    }

    return RawActionData(
        id = action.id,
        category = "no-category",
        args = emptyList(),
        type = type
    )
}

data class ActionCompound(
    val actionData: ActionData,
    val rawActionData: RawActionData
)


