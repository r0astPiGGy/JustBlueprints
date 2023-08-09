package com.rodev.jbpkmp.domain.model.variable

import com.rodev.jbp.compiler.module.value.VariableConstant
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed interface Variable {
    val id: String
    val name: String

    @Transient
    val type: Type

    fun toVariableConstant() : VariableConstant

    enum class Type(val typeName: String) {
        Local("Локальная переменная"),
        Game("Игровая переменная"),
        Save("Сохранённая переменная")
    }
}