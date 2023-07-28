package com.rodev.generator.action.interpreter

import com.rodev.generator.action.ActionLogger
import com.rodev.jmcc_extractor.entity.ActionData
import com.rodev.jmcc_extractor.entity.RawActionData

class ActionCategoryResolver(
    rawActions: List<RawActionData>
) {

    private val mappedActions = HashMap<String, RawActionData>()

    init {
        rawActions.forEach {
            mappedActions[it.id] = it
        }
    }

    fun resolveCategoryFor(actionData: ActionData): String? {
        val mappedAction = mappedActions[actionData.id]

        if (mappedAction == null) {
            ActionLogger.log("Category not found for action: ${actionData.id}")
        }

        return mappedAction?.getCategoryPath()
    }

}