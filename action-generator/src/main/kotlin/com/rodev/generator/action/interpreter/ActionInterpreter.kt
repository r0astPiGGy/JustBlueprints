package com.rodev.generator.action.interpreter

import com.rodev.generator.action.LocaleProvider
import com.rodev.generator.action.entity.Action
import com.rodev.jmcc_extractor.entity.ActionData

class ActionInterpreter(
    private val actionCategoryResolver: ActionCategoryResolver,
    private val localeProvider: LocaleProvider
) : ListInterpreter<ActionData, Action>() {

    override fun interpretElement(input: ActionData): Action {
        return Action(
            id = input.id,
            name = localeProvider.translateActionName(input),
            input = emptySet(),
            output = emptySet(),
            iconNamespace = "actions",
            category = actionCategoryResolver.resolveCategoryFor(input).toString()
        )
    }

}