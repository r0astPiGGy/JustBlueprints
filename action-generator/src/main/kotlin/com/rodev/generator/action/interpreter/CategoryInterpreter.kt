package com.rodev.generator.action.interpreter

import com.rodev.generator.action.LocaleProvider
import com.rodev.generator.action.entity.Category
import com.rodev.jmcc_extractor.entity.RawActionData

class CategoryInterpreter(
    private val localeProvider: LocaleProvider
) : ListInterpreter<RawActionData, Category>() {

    override fun interpret(input: List<RawActionData>): List<Category> {
        return super.interpret(input).distinct()
    }

    override fun interpretElement(input: RawActionData): Category {
        return Category(
            path = input.getCategoryPath(),
            name = if (input.subcategory == null)
                localeProvider.translateCategory(input)
            else
                localeProvider.translateSubCategory(input)
        )
    }

}

fun RawActionData.getCategoryPath(): String {
    var path = category

    subcategory?.let { path += ".$it" }

    return path
}