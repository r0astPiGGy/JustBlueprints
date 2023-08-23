package com.rodev.generator.action

import com.rodev.generator.action.entity.Action
import com.rodev.jmcc_extractor.entity.*
import java.util.*

class LocaleProvider(
    private val localeDataSource: LocaleDataSource
) {

    fun translateActionName(action: ActionData): String {
        return localeDataSource.getOrDefault("creative_plus.action.${action.id}.name")
    }

    fun translateActionDescription(action: ActionData): String {
        return localeDataSource.getOrDefault("creative_plus.action.${action.id}.description")
    }

    fun translateActionAdditionalInformation(rawActionData: RawActionData): List<String> {
        val additionalInfo = rawActionData.additionalInfo ?: return emptyList()

        return additionalInfo.map {
            localeDataSource.getOrDefault("creative_plus.action.${rawActionData.id}.additional_information.$it")
        }
    }

    fun translateActionWorksWith(rawActionData: RawActionData): List<String> {
        val worksWith = rawActionData.worksWith ?: return emptyList()

        return worksWith.map {
            localeDataSource.getOrDefault("creative_plus.action.${rawActionData.id}.work_with.$it")
        }
    }

    fun translateArgName(actionData: ActionData, arg: Argument): String {
        val localeId = String.format("creative_plus.action.%s.argument.%s.name", actionData.id, arg.name)
        return localeDataSource.getOrDefault(localeId)
    }

    fun translateCategory(category: String): String {
        val split = category.split(".")

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

    fun translateEventDescription(eventData: EventData): String {
        val key = "creative_plus.trigger.${eventData.id}.description"

        return localeDataSource.getOrDefault(key)
    }

    fun translateEventAdditionalInformation(eventData: EventData): List<String> {
        val info = mutableListOf<String>()
        if (eventData.cancellable)
            info.add("Отменяемое")

        val additionalInfo = eventData.additionalInfo ?: return info

        val mappedInfo = additionalInfo.map {
            localeDataSource.getOrDefault("creative_plus.trigger.${eventData.id}.additional_information.$it")
        }

        info.addAll(mappedInfo)

        return info
    }

    fun translateEventWorksWith(eventData: EventData): List<String> {
        val worksWith = eventData.worksWith ?: return emptyList()

        return worksWith.map {
            localeDataSource.getOrDefault("creative_plus.trigger.${eventData.id}.work_with.$it")
        }
    }

    fun translateGameValue(gameValueData: GameValueData): String {
        val key = String.format("creative_plus.game_value.%s.name", gameValueData.id)

        return localeDataSource.getOrDefault(key)
    }

    fun translateGameValueDescription(gameValueData: GameValueData): String? {
        val key = String.format("creative_plus.game_value.%s.description", gameValueData.id)

        return localeDataSource.get(key)
    }

    fun translateGameValueWorksWith(gameValue: GameValueData): List<String> {
        val worksWith = gameValue.worksWith ?: return emptyList()

        return worksWith.map {
            localeDataSource.getOrDefault("creative_plus.game_value.${gameValue.id}.work_with.$it")
        }
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