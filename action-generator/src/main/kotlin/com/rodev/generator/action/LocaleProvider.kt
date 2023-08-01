package com.rodev.generator.action

import com.rodev.generator.action.entity.Action
import com.rodev.jmcc_extractor.entity.ActionData
import com.rodev.jmcc_extractor.entity.Argument
import com.rodev.jmcc_extractor.entity.EventData
import com.rodev.jmcc_extractor.entity.GameValueData
import java.util.*

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

    fun translateCategory(action: Action): String {
        val split = action.category.split(".")

        return if (split.size < 2) {
            val key = resolveTranslationKeyForCategory(split[0]) + ".name"
            localeDataSource.getOrDefault(key)
        } else {
            translateSubCategory(split[0], split[1])
        }
    }

    fun translateEventName(eventData: EventData): String {
        val key = "creative_plus.trigger.${eventData.id}.name"

        return localeDataSource.getOrDefault(key)
    }

    fun translateGameValue(gameValueData: GameValueData): String {
        val key = String.format("creative_plus.game_value.%s.name", gameValueData.id)

        return localeDataSource.getOrDefault(key)
    }

    private fun translateSubCategory(parentCategory: String, category: String): String {
        val parentKey = resolveTranslationKeyForCategory(parentCategory)
        val localeKey = String.format("%s.subcategory.%s.name", parentKey, category)
        return localeDataSource.getOrDefault(localeKey)
    }

    fun translateEnumName(actionData: ActionData, arg: Argument, rawEnumValue: String): String {
        val key = String.format(
            "creative_plus.action.%s.argument.%s.enum.%s.name",
            actionData.id, arg.name, rawEnumValue.lowercase(Locale.getDefault())
        )

        return localeDataSource.getOrDefault(key)
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