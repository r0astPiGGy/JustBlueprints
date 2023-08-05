package com.rodev.generator.action.entity

import com.rodev.generator.action.utils.toMap

enum class SelectorType(
    val id: String
) {
    Entity("entity"),
    Player("player"),
    GameValue("game_value")

    ;

    companion object {

        private val selectorTypeByIds: Map<String, SelectorType> = values().toList().toMap(SelectorType::id)

        fun fromId(id: String): SelectorType? = selectorTypeByIds[id]
    }
}