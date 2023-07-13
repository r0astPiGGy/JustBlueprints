package com.rodev.jbpkmp.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.rodev.jbpkmp.data.NodeEntity
import com.rodev.jbpkmp.data.PinEntity
import com.rodev.jbpkmp.presentation.components.node.NodeState
import java.util.*
import kotlin.random.Random.Default.nextInt

class ViewPortViewModel {

    private val _nodeStates = mutableStateListOf<NodeState>()
    val nodeStates: List<NodeState>
        get() = _nodeStates

    private var counter = 0

    fun onNodeAdd() {
        _nodeStates.add(NodeState(randomNode()))
    }

    private fun randomNode(): NodeEntity {
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
        val size = nextInt(0, 5)

        val list = ArrayList<PinEntity>()

        for (i in 0..size) {
             list += randomPinEntity(i)
        }

        return list
    }

    private fun randomColor(): Int {
        return Color(
            red = nextInt(),
            green = nextInt(),
            blue = nextInt()
        ).toArgb()
    }

    private fun randomPinEntity(index: Int): PinEntity {
        return PinEntity(
            id = UUID.randomUUID().toString(),
            color = randomColor(),
            name = "Pin${index}"
        )
    }


}