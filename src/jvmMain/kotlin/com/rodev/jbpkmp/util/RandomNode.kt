package com.rodev.jbpkmp.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.rodev.jbpkmp.data.ConnectionType
import com.rodev.jbpkmp.data.NodeEntity
import com.rodev.jbpkmp.data.PinEntity
import java.util.*
import kotlin.random.Random

var counter = 0

fun randomNode(): NodeEntity {
    return NodeEntity(
        id = UUID.randomUUID().toString(),
        header = "Header${counter++}",
        headerColor = randomColor(),
        x = Random.nextFloat() * 800f,
        y = Random.nextFloat() * 800f,
        inputPins = randomPins(ConnectionType.INPUT),
        outputPins = randomPins(ConnectionType.OUTPUT)
    )
}

private fun randomPins(connectionType: ConnectionType): List<PinEntity> {
    val size = Random.nextInt(1, 2)

    val list = ArrayList<PinEntity>()

    list += PinEntity(
        id = UUID.randomUUID().toString(),
        color = Color.White.toArgb(),
        name = "Exec",
        connectionType = connectionType,
        supportsMultipleConnection = false
    )

    for (i in 0..size) {
        list += randomPinEntity(i, connectionType)
    }

    return list
}

private fun randomColor(): Int {
    return Color(
        red = Random.nextInt(),
        green = Random.nextInt(),
        blue = Random.nextInt()
    ).toArgb()
}

private fun randomPinEntity(index: Int, connectionType: ConnectionType): PinEntity {
    return PinEntity(
        id = UUID.randomUUID().toString(),
        color = randomColor(),
        name = "Pin${index}",
        connectionType = connectionType
    )
}