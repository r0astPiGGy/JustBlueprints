package com.rodev.jbpkmp.domain.model

import com.rodev.generator.action.entity.PinType

data class PinEntity(
    val id: String,
    val color: Int,
    val name: String,
    val type: PinType
)
