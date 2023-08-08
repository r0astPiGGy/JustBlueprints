package com.rodev.generator.action.interpreter.category

import com.rodev.generator.action.LocaleProvider
import com.rodev.generator.action.entity.Action
import com.rodev.generator.action.entity.Category
import com.rodev.generator.action.interpreter.Interpreter
import com.rodev.generator.action.interpreter.ListInterpreter
import com.rodev.jmcc_extractor.entity.RawActionData

class CategoryInterpreter(
    private val localeProvider: LocaleProvider
) : Interpreter<List<Action>, List<Category>> {

    override fun interpret(input: List<Action>): List<Category> {
        val list = mutableListOf<Category>()

        for (action in input) {
            val categories = action.category.split(".")

            val category = Category(
                path = categories[0],
                name = localeProvider.translateCategory(categories[0])
            )

            list.add(category)

            if (categories.size > 1) {
                list.add(Category(
                    path = action.category,
                    localeProvider.translateCategory(action.category)
                ))
            }

        }

        return list.distinct()
    }

}

fun RawActionData.getCategoryPath(): String {
    var path = category

    subcategory?.let { path += ".$it" }

    return path
}