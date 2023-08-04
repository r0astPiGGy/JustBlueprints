package com.rodev.jbpkmp.domain.model

import com.rodev.generator.action.entity.PinType
import com.rodev.nodeui.model.ConnectionType

data class PinEntity(
    val id: String,
    val color: Int,
    val name: String,
    val type: PinType
)
