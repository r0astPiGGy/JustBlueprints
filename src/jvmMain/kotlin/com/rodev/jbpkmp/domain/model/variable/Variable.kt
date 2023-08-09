package com.rodev.jbpkmp.domain.model.variable

import com.rodev.jbp.compiler.module.value.VariableConstant
import kotlinx.serialization.Serializable

@Serializable
sealed interface Variable {
    val id: String
    val name: String

    fun toVariableConstant() : VariableConstant
}