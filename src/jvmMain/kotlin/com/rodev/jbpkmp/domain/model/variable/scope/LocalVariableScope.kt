package com.rodev.jbpkmp.domain.model.variable.scope

import com.rodev.jbpkmp.domain.model.variable.LocalVariable
import kotlinx.serialization.Serializable

@Serializable
data class LocalVariableScope(
    val parentId: String,
    override val variables: List<LocalVariable>
) : VariableScope