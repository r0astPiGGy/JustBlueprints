package com.rodev.jbpkmp.presentation.components.graph

import com.rodev.jbpkmp.data.NodeEntity
import com.rodev.jbpkmp.data.PinEntity
import com.rodev.jbpkmp.presentation.components.pin.PinStateFactory
import com.rodev.jbpkmp.presentation.components.pin.row.PinRowStateFactory

// TODO сохранение/загрузка Graph
class GraphFactory(
    private val pinRowStateFactory: PinRowStateFactory,
    private val pinStateFactory: PinStateFactory,
    private val pinEntityProvider: PinEntityProvider,
    private val nodeEntityProvider: NodeEntityProvider
) {

//    fun save(pins: List<PinState>, wires: List<PinWire>): Graph {
//        val nodes = HashMap<UUID, Node>()
//
//        pins.forEach {
//            val node: Node
//
//            with (it.getNode()) {
//                node = if (!nodes.containsKey(id)) {
//                    Node(
//                        x = x,
//                        y = y,
//                        uniqueId = id.toString(),
//                        typeId = nodeEntity.id,
//                        mutableListOf(),
//                        mutableListOf()
//                    )
//                } else {
//                    nodes[id]!!
//                }
//            }
//
//            val pin = Pin(
//                uniqueId = it.id.toString(),
//                typeId = it.entity.id,
//                value = it.defaultValueComposable.getValue()
//            )
//
//            if (it.isInput()) {
//                (node.inputPins as MutableList).add(pin)
//            } else if (it.isOutput()) {
//                (node.outputPins as MutableList).add(pin)
//            }
//        }
//
//        val connections = wires.map {
//            PinConnection(
//                it.inputPin.id.toString(),
//                it.outputPin.id.toString()
//            )
//        }
//
//        return Graph(
//            connections = connections,
//            nodes = nodes.values.toList()
//        )
//    }
//
//    fun load(graph: Graph) {
//        val pinsById = mutableMapOf<UUID, PinState>()
//        val pinGetter: (String) -> PinState? = {
//            pinsById[UUID.fromString(it)]
//        }
//
//        val nodes = graph.nodes.map { node ->
//            val nodeState = node.toState()
//
//            val pinStateFunc: (List<Pin>) -> Unit = { pins ->
//                pins.map { it.toState(nodeState) }
//                    .forEach { pinsById[it.id] = it }
//            }
//
//            pinStateFunc(node.inputPins)
//            pinStateFunc(node.outputPins)
//        }
//
//        val connections = graph.connections.map { connection ->
//            val inputPin = pinGetter(connection.inputPinId)
//            val outputPin = pinGetter(connection.outputPinId)
//
//            PinWire(
//                inputPin = requireNotNull(inputPin),
//                outputPin = requireNotNull(outputPin)
//            )
//        }
//    }
//
//    private fun getNodeEntityById(id: String): NodeEntity {
//        return nodeEntityProvider.getNodeEntity(id)
//    }
//
//    private fun getPinEntityById(nodeId: String, id: String): PinEntity {
//        return pinEntityProvider.getPinEntity(nodeId, id)
//    }
//
//    private fun Pin.toState(nodeState: NodeState): PinState {
//        return pinStateFactory.createOrGetPinState(
//            id = UUID.fromString(uniqueId),
//            pinRowState = pinRowStateFactory.createInputPinRowState(nodeState),
//            pinEntity = getPinEntityById(nodeState.nodeEntity.id, typeId)
//        )
//    }
//
//    private fun Node.toState(): NodeState {
//        return NodeState(
//            id = UUID.fromString(uniqueId),
//            nodeEntity = getNodeEntityById(typeId),
//            initialX = x,
//            initialY = y
//        )
//    }

}

interface NodeEntityProvider {

    fun getNodeEntity(typeId: String): NodeEntity

}

interface PinEntityProvider {

    fun getPinEntity(nodeId: String, typeId: String): PinEntity

}