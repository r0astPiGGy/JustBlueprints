package com.rodev.nodeui.components.graph

import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.components.node.NodeStateFactory
import com.rodev.nodeui.components.pin.PinState
import com.rodev.nodeui.components.pin.row.PinRowState
import com.rodev.nodeui.components.wire.PinWire
import com.rodev.nodeui.model.Graph
import com.rodev.nodeui.model.PinConnection

typealias PinConnector = (inputPin: PinState, outputPin: PinState) -> Unit

class GraphFactory(
    private val nodeStateFactory: NodeStateFactory
) {

    fun save(nodeStates: List<NodeState>, wires: List<PinWire>): Graph {
        val nodes = nodeStates.map { it.nodeDisplay.toNode(it) }

        val connections = wires.map {
            PinConnection(
                it.inputPin.id,
                it.outputPin.id
            )
        }

        return Graph(
            connections = connections,
            nodes = nodes
        )
    }

    fun load(graph: Graph, pinConnector: PinConnector): List<NodeState> {
        val pinStatesById = hashMapOf<String, PinState>()
        val nodeStates = mutableListOf<NodeState>()

        fun mapPins(pinRows: Collection<PinRowState>) {
            pinRows.map { it.pinState }.forEach { pinStatesById[it.id] = it }
        }

        graph.nodes.forEach {
            val nodeState = nodeStateFactory.createNodeState(it)
            with(nodeState) {
                inputPins.let(::mapPins)
                outputPins.let(::mapPins)
                nodeStates.add(this)
            }
        }

        graph.connections.forEach {
            val inputPinState = pinStatesById[it.inputPinId]!!
            val outputPinState = pinStatesById[it.outputPinId]!!

            pinConnector(inputPinState, outputPinState)
        }

        return nodeStates
    }
}