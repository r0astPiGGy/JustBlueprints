package com.rodev.jbpkmp.domain.model.variable

import kotlinx.serialization.Serializable

@Serializable
data class GlobalVariable(
    override val id: String,
    override val name: String,
    override val value: String? = null,
    val type: Type
) : Variable {

    enum class Type(val typeName: String) {
        SAVED("Сохранённая переменная"),
        GAME("Игровая переменная")
    }

}
