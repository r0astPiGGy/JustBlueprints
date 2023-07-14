package com.rodev.jbpkmp.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
        x = 0f,
        y = 0f,
        inputPins = randomPins(),
        outputPins = randomPins()
    )
}

private fun randomPins(): List<PinEntity> {
    val size = Random.nextInt(1, 2)

    val list = ArrayList<PinEntity>()

    for (i in 0..size) {
        list += randomPinEntity(i)
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

private fun randomPinEntity(index: Int): PinEntity {
    return PinEntity(
        id = UUID.randomUUID().toString(),
        color = randomColor(),
        name = "Pin${index}"
    )
}