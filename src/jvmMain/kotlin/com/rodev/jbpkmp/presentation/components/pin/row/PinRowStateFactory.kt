package com.rodev.jbpkmp.presentation.components.pin.row

import com.rodev.jbpkmp.presentation.components.node.NodeState

interface PinRowStateFactory {

    fun createPinRowState(nodeState: NodeState): PinRowState

}

object DefaultRowStateFactory : PinRowStateFactory {
    override fun createPinRowState(nodeState: NodeState): PinRowState {
        return PinRowState(nodeState)
    }
}