package com.rodev.jbpkmp.domain.model.variable

import com.rodev.jbp.compiler.module.value.VariableConstant
import kotlinx.serialization.Serializable

@Serializable
data class GlobalVariable(
    override val id: String,
    override val name: String,
    val type: Type
) : Variable {

    enum class Type(val typeName: String) {
        SAVED("Сохранённая переменная"),
        GAME("Игровая переменная")
    }

    override fun toVariableConstant() = VariableConstant(
        name,
        if (type == Type.SAVED) VariableConstant.Scope.SAVE else VariableConstant.Scope.GAME
    )

}
