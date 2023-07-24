package com.rodev.jbpkmp.domain.model

import com.rodev.nodeui.model.ConnectionType

data class PinEntity(
    val id: String,
    val color: Int,
    val name: String,
    val connectionType: ConnectionType,
    val supportsMultipleConnection: Boolean = connectionType != ConnectionType.INPUT,
    val type: Any = PinType
)

// TODO
object PinType
