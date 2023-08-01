package com.rodev.generator.action.interpreter.game_value

import com.rodev.generator.action.LocaleProvider
import com.rodev.generator.action.anyToDynamic
import com.rodev.generator.action.entity.NodeCompound
import com.rodev.generator.action.entity.PinModel
import com.rodev.generator.action.entity.extra_data.DictionaryExtraData
import com.rodev.generator.action.entity.extra_data.ExtraData
import com.rodev.generator.action.entity.extra_data.ListExtraData
import com.rodev.generator.action.entity.extra_data.SelectorDisabledExtraData
import com.rodev.generator.action.entity.iconPathFrom
import com.rodev.generator.action.interpreter.ActionInterpreter
import com.rodev.jmcc_extractor.entity.GameValueData

class GameValueInterpreter(
    private val localeProvider: LocaleProvider
) : ActionInterpreter<GameValueData> {

    private val gameValuesWithDisabledSelector: Set<String>

    init {
        gameValuesWithDisabledSelector = hashSetOf(
            "cpu_usage",
            "server_tps",
            "timestamp",
            "server_current_tick",
            "selection_size",
            "selection_target_names",
            "selection_target_uuids",
            "url_response",
            "url_response_code",
            "url",
            "world_time",
            "world_weather",
            "server_stopped_time",
            "action_count_per_tick",
            "owner_uuid",
            "world_size",
            "world_id"
        )
    }

    override fun interpret(list: List<GameValueData>) = list.interpret(::interpretGameValue)

    private fun interpretGameValue(gameValue: GameValueData): NodeCompound {
        val name = localeProvider.translateGameValue(gameValue)

        return NodeCompound(
            id = gameValue.id + "_gamevalue_getter",
            type = "game_value_getter",
            name = name,
            input = emptyList(),
            output = listOf(gameValue.output),
            iconPath = iconPathFrom("game_values", gameValue.id),
            category = "game_values",
            extra = gameValue.nodeExtraData
        )
    }

    private val GameValueData.output: PinModel
        get() {
            return PinModel(
                id = id,
                label = "",
                type = type,
                extra = outputPinExtraData
            )
        }

    private val GameValueData.nodeExtraData: ExtraData?
        get() {
            if (gameValuesWithDisabledSelector.contains(id) || id.startsWith("event_")) {
                return SelectorDisabledExtraData
            }
            return null
        }
}

val GameValueData.outputPinExtraData: ExtraData?
    get() {
        return when (type) {
            "dictionary" -> DictionaryExtraData(
                keyType = keyType!!.anyToDynamic(),
                elementType = valueType!!.anyToDynamic()
            )
            "list" -> ListExtraData(
                elementType = elementType!!.anyToDynamic()
            )
            else -> null
        }
    }