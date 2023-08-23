package com.rodev.jbpkmp.domain.compiler

import com.rodev.jbp.compiler.module.value.Value
import com.rodev.jbp.compiler.module.value.constants.*
import com.rodev.jbpkmp.presentation.screens.editor_screen.getId
import com.rodev.jbpkmp.presentation.screens.editor_screen.getValue
import com.rodev.nodeui.model.Node

object ValueFactory {
    fun createValue(factory: Nodes.Factory, node: Node): Value {
        return when (factory) {
            Nodes.Factory.ITEM -> createItem(node)
            Nodes.Factory.LOCATION -> createLocation(node)
            Nodes.Factory.TEXT -> createText(node)
            Nodes.Factory.NUMBER -> createNumber(node)
            Nodes.Factory.SOUND -> createSound(node)
            Nodes.Factory.PARTICLE -> createParticle(node)
            Nodes.Factory.POTION -> createPotion(node)
            Nodes.Factory.VECTOR -> createVector(node)
            Nodes.Factory.ARRAY -> throw IllegalStateException()
        }
    }

    private fun createVector(node: Node): Value {
        return VectorConstant(
            x = node.getDouble("x"),
            y = node.getDouble("y"),
            z = node.getDouble("z"),
        )
    }

    private fun createPotion(node: Node): Value {
        return PotionConstant(
            potion = node.getStringOrDefault("potion", ""),
            amplifier = node.getDouble("amplifier"),
            duration = node.getDouble("duration"),
        )
    }

    private fun createParticle(node: Node): Value {
        TODO()
    }

    private fun Node.findStringById(id: String): String? {
        val pin = inputPins.find { it.getId() == id } ?: outputPins.find { it.getId() == id }

        return pin?.getValue()
    }

    private fun Node.getStringOrDefault(id: String, default: String): String {
        return findStringById(id) ?: default
    }

    private fun Node.getDoubleOrDefault(id: String, default: Double): Double {
        return findStringById(id)?.toDoubleOrNull() ?: default
    }

    private fun Node.getDouble(id: String): Double {
        return getDoubleOrDefault(id, 0.0)
    }

    private fun createItem(node: Node): ItemConstant {
        val itemJson = node.getStringOrDefault("item-json", "{}")

        return ItemConstant(itemJson)
    }

    private fun createLocation(node: Node): LocationConstant {
        return LocationConstant(
            x = node.getDouble("x"),
            y = node.getDouble("y"),
            z = node.getDouble("z"),
            yaw = node.getDouble("yaw"),
            pitch = node.getDouble("pitch")
        )
    }

    private fun createText(node: Node): TextConstant {
        return TextConstant(node.getStringOrDefault("text", ""))
    }

    private fun createNumber(node: Node): NumberConstant {
        return NumberConstant(node.getDouble("number"))
    }

    private fun createSound(node: Node): SoundConstant {
        return SoundConstant(
            sound = node.getStringOrDefault("sound", ""),
            volume = node.getDouble("volume"),
            pitch = node.getDouble("pitch")
        )
    }

}