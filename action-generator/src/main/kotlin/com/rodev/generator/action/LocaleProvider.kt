package com.rodev.generator.action

import com.rodev.jmcc_extractor.entity.ActionData
import com.rodev.jmcc_extractor.entity.Argument
import com.rodev.jmcc_extractor.entity.RawActionData

class LocaleProvider(
    private val localeDataSource: LocaleDataSource
) {

    fun translateActionName(action: ActionData): String {
        return localeDataSource.getOrDefault("creative_plus.action.${action.id}.name")
    }

    fun translateArgName(actionData: ActionData, arg: Argument): String {
        val localeId = String.format("creative_plus.action.%s.argument.%s.name", actionData.id, arg.name)
        return localeDataSource.getOrDefault(localeId)
    }

    fun translateCategory(rawActionData: RawActionData): String {
        val key = resolveTranslationKeyForCategory(rawActionData.category) + ".name"
        return localeDataSource.getOrDefault(key)
    }

    fun translateSubCategory(rawActionData: RawActionData): String {
        val parentKey = resolveTranslationKeyForCategory(rawActionData.category)
        val localeKey = String.format("%s.subcategory.%s.name", parentKey, rawActionData.subcategory)
        return localeDataSource.getOrDefault(localeKey)
    }

    private fun resolveTranslationKeyForCategory(category: String): String {
        val localeKey = "creative_plus.category.$category"

        val translated = localeDataSource.get("$localeKey.name")

        return if (translated != null) localeKey else localeKey + "_action"
    }

}

interface LocaleDataSource {

    fun getOrDefault(id: String): String {
        return get(id) ?: id
    }

    fun get(id: String): String?

}

fun Map<String, String>.toLocaleDataSource(): LocaleDataSource {
    return object : LocaleDataSource {
        override fun get(id: String): String? {
            return this@toLocaleDataSource[id]
        }
    }
}