package com.rodev.jbp.compiler.module.value

import com.rodev.jbp.compiler.json.inheritBuilder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class GameValue(
    val value: String,
    val selection: String?
) : CValue(ValueType.GameValue) {

    override fun toJson(): JsonObject {
        return super.toJson().inheritBuilder {
            put("game_value", value)
            // Зачем-то значение селектора встраивается как строка
            put("selection", stringifySelection())
        }
    }

    private fun stringifySelection(): String {
        return buildJsonObject {
            put("type", selection ?: DEFAULT_SELECTION)
        }.toString()
    }

    companion object {

        const val DEFAULT_SELECTION = "default"

    }
}