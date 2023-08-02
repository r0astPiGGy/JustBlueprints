package com.rodev.jbpkmp.domain.model.saveable_variable

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
