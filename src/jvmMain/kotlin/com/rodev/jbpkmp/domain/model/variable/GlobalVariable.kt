package com.rodev.jbpkmp.domain.model.variable

import com.rodev.jbp.compiler.module.value.VariableConstant
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class GlobalVariable(
    override val id: String,
    override val name: String,
    @Transient
    override val type: Variable.Type = Variable.Type.Game,
) : Variable {

    override fun toVariableConstant() = VariableConstant(
        name,
        if (type == Variable.Type.Save) VariableConstant.Scope.SAVE else VariableConstant.Scope.GAME
    )

}
