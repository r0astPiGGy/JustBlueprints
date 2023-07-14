package com.rodev.jbpkmp.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerInputChange
import com.rodev.jbpkmp.data.NodeEntity
import com.rodev.jbpkmp.data.PinEntity
import com.rodev.jbpkmp.presentation.components.Wire
import com.rodev.jbpkmp.presentation.components.node.NodeState
import com.rodev.jbpkmp.presentation.components.pin.PinDragHandler
import com.rodev.jbpkmp.presentation.components.pin.PinState
import java.util.*
import kotlin.random.Random.Default.nextInt

class ViewPortViewModel : PinDragHandler {

    private val _nodeStates = mutableStateListOf<NodeState>()
    val nodeStates: List<NodeState>
        get() = _nodeStates

    private val _temporaryLine = mutableStateOf<Wire?>(null)
    val temporaryLine: State<Wire?>
        get() = _temporaryLine

    private var counter = 0

    private var currentDraggingPin: PinState? = null

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
        val size = nextInt(-1, 2)

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

    override fun onDragStart(pinState: PinState) {
        currentDraggingPin = pinState
    }

    override fun onDrag(pinState: PinState, offset: Offset, change: PointerInputChange) {
        require(pinState == currentDraggingPin)

        val pos = pinState.position

        _temporaryLine.value = Wire(
            pos.x,
            pos.y,
            pos.x + change.position.x,
            pos.y + change.position.y
        )
    }

    override fun onEnd() {
        currentDraggingPin = null
        _temporaryLine.value = null
    }


}