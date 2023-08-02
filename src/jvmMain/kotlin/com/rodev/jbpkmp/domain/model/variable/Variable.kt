package com.rodev.jbpkmp.domain.model.variable

import kotlinx.serialization.Serializable

@Serializable
sealed interface Variable {
    val id: String
    val name: String
    val value: Any?
}