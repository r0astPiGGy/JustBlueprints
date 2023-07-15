package com.rodev.jbpkmp.data

data class PinEntity(
    val id: String,
    val color: Int,
    val name: String,
    val connectionType: ConnectionType,
    val supportsMultipleConnection: Boolean = connectionType != ConnectionType.INPUT
)

enum class ConnectionType {
    INPUT,
    OUTPUT
}
