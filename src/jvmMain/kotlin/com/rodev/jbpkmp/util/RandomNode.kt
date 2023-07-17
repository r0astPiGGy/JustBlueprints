package com.rodev.jbpkmp.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.rodev.jbpkmp.data.*
import java.util.*
import kotlin.random.Random

var counter = 0

fun randomNode(): Node {
    return Node(
        x = 0f,
        y = 0f,
        uniqueId = UUID.randomUUID().toString(),
        typeId = "bebra",
        inputPins = randomPins(),
        outputPins = randomPins()
    )
}

fun randomNodeEntity(): NodeEntity {
    return NodeEntity(
        id = UUID.randomUUID().toString(),
        header = "Header${counter++}",
        headerColor = randomColor()
    )
}

private fun randomPins(): List<Pin> {
    val size = Random.nextInt(1, 2)

    val list = ArrayList<Pin>()

    for (i in 0..size) {
        list += Pin(
            uniqueId = UUID.randomUUID().toString(),
            typeId = UUID.randomUUID().toString(),
            value = null
        )
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

fun randomPinEntity(connectionType: ConnectionType): PinEntity {
    return PinEntity(
        id = UUID.randomUUID().toString(),
        color = randomColor(),
        name = "Pin${Random.nextInt()}",
        connectionType = connectionType
    )
}