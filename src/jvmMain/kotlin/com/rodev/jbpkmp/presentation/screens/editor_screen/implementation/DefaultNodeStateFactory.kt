package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation

import com.rodev.jbpkmp.util.randomNodeEntity
import com.rodev.nodeui.components.node.NodeRepresentation
import com.rodev.nodeui.components.node.NodeStateFactory
import com.rodev.nodeui.components.pin.row.PinRowStateFactory

class DefaultNodeStateFactory(pinRowStateFactory: PinRowStateFactory) : NodeStateFactory(pinRowStateFactory) {

    override fun getNodeRepresentation(typeId: String): NodeRepresentation {
        return DefaultNodeRepresentation(randomNodeEntity())
    }

}

