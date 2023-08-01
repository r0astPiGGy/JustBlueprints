package com.rodev.generator.action.interpreter.category

import com.rodev.generator.action.LocaleProvider
import com.rodev.generator.action.entity.Action
import com.rodev.generator.action.entity.Category
import com.rodev.generator.action.interpreter.ListInterpreter
import com.rodev.jmcc_extractor.entity.RawActionData

class CategoryInterpreter(
    private val localeProvider: LocaleProvider
) : ListInterpreter<Action, Category>() {

    override fun interpret(input: List<Action>): List<Category> {
        return super.interpret(input).distinct()
    }

    override fun interpretElement(input: Action): Category {
        return Category(
            path = input.category,
            name = localeProvider.translateCategory(input)
        )
    }

}

fun RawActionData.getCategoryPath(): String {
    var path = category

    subcategory?.let { path += ".$it" }

    return path
}