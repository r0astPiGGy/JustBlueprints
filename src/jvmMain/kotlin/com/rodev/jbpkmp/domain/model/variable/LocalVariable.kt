package com.rodev.jbpkmp.domain.model.variable

import kotlinx.serialization.Serializable

@Serializable
data class LocalVariable(
    override val id: String,
    override val name: String
) : Variable
