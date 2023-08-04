package com.rodev.nodeui.components.pin

import androidx.compose.runtime.Composable
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.model.ConnectionType
import com.rodev.nodeui.model.Pin

interface PinDisplay {

    val name: String

    val color: Int

    val type: Any?

    @Composable
    fun PinView(nodeState: NodeState, pinState: PinState)

    fun toPin(pinState: PinState): Pin

}