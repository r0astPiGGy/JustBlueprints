package com.rodev.jbpkmp.domain.model.saveable_variable

import kotlinx.serialization.Serializable

@Serializable
sealed interface Variable {
    val id: String
    val name: String
}