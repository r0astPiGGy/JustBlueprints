package com.rodev.jbpkmp.domain.model.variable

import kotlinx.serialization.Serializable

@Serializable
data class GlobalVariable(
    override val id: String,
    override val name: String,
    val type: Type
) : Variable {

    enum class Type {
        SAVED,
        GAME
    }

}
