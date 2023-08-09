package com.rodev.jbpkmp.domain.model.variable

import com.rodev.jbp.compiler.module.value.VariableConstant
import kotlinx.serialization.Serializable

@Serializable
data class LocalVariable(
    override val id: String,
    override val name: String
) : Variable {

    override val type: Variable.Type = Variable.Type.Local

    override fun toVariableConstant() = VariableConstant(name, VariableConstant.Scope.LOCAL)
}
