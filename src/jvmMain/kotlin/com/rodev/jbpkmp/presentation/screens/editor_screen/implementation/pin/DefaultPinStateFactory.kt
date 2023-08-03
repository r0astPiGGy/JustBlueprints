package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.pin

import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.rodev.generator.action.entity.NodeModel
import com.rodev.generator.action.entity.PinModel
import com.rodev.generator.action.entity.extra_data.EnumExtraData
import com.rodev.jbpkmp.domain.model.PinEntity
import com.rodev.jbpkmp.domain.repository.*
import com.rodev.jbpkmp.presentation.localization.name
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.BooleanInputComposable
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.EnumInputComposable
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.StringInputComposable
import com.rodev.nodeui.components.pin.*
import com.rodev.nodeui.model.ConnectionType
import com.rodev.nodeui.model.Node
import com.rodev.nodeui.model.Pin

class DefaultPinStateFactory(
    private val nodeDataSource: NodeDataSource,
    private val pinTypeDataSource: PinTypeDataSource,
    private val defaultValueComposableRegistry: DefaultValueComposableRegistry = createDefaultValueComposableRegistry()
) : PinStateFactory() {

    override fun createDefaultValueComposable(
        node: Node,
        pin: Pin,
        connectionType: ConnectionType
    ): DefaultValueComposable {
        if (connectionType == ConnectionType.INPUT) {
            val nodeModel = nodeDataSource.getNodeModelById(node.typeId)
            // can be optimized
            val pinModel = nodeModel.findPinModelById(pin.typeId, connectionType)

            return defaultValueComposableRegistry.create(pinModel).visibleIfNotConnected()
        }

        return super.createDefaultValueComposable(node, pin, connectionType)
    }

    override fun createOutputPinState(node: Node, pin: Pin): PinState {
        if (node.typeId == variableTypeId) {
            return createVariablePinState(pin)
        }

        return super.createOutputPinState(node, pin)
    }

    private fun createVariablePinState(pin: Pin): PinState {
        return PinState(
            id = pin.uniqueId,
            pinRepresentation = createVariablePinRepresentation(pin),
            defaultValueComposable = EmptyDefaultValueComposable
        )
    }

    private fun createVariablePinRepresentation(pin: Pin): PinRepresentation {
        val pinType = pinTypeDataSource["variable"]!!

        val pinEntity = PinEntity(
            id = pin.typeId,
            color = pinType.color,
            name = "",
            connectionType = ConnectionType.OUTPUT,
            type = pinType
        )

        return DefaultPinRepresentation(pinEntity, ConnectionType.OUTPUT, DefaultDrawFunction)
    }

    override fun createPinRepresentation(node: Node, pin: Pin, connectionType: ConnectionType): PinRepresentation {
        val nodeModel = nodeDataSource.getNodeModelById(node.typeId)
        val pinModel = nodeModel.findPinModelById(pin.typeId, connectionType)
        val pinType = pinTypeDataSource[pinModel.type]!!

        val execPin = pinType.id == "exec"

        val pinEntity = PinEntity(
            id = pin.typeId,
            color = pinType.color,
            name = pinModel.label,
            connectionType = connectionType,
            supportsMultipleConnection = !execPin && connectionType != ConnectionType.INPUT,
            type = pinType
        )

        val drawFunction: PinDrawFunction = if (execPin) {
            ExecDrawFunction
        } else {
            DefaultDrawFunction
        }

        return DefaultPinRepresentation(pinEntity, connectionType, drawFunction)
    }

    private fun NodeModel.findPinModelById(id: String, connectionType: ConnectionType): PinModel {
        return when (connectionType) {
            ConnectionType.INPUT -> findPinModelByIdIn(input, id)
            ConnectionType.OUTPUT -> findPinModelByIdIn(output, id)
        }
    }

    private fun findPinModelByIdIn(pins: List<PinModel>, id: String): PinModel {
        return pins.find { it.id == id }!!
    }
}

private fun createDefaultValueComposableRegistry() = DefaultValueComposableRegistry.create {
    register("text") {
        StringInputComposable()
    }
    register("enum") {
        val extra = it.extra as EnumExtraData

        EnumInputComposable(extra.values)
    }
    register("boolean") {
        BooleanInputComposable()
    }
}

private object DefaultDrawFunction : PinDrawFunction {
    override fun DrawScope.onDraw(pinState: PinState) {
        drawCircle(
            color = Color(pinState.pinRepresentation.color),
            style = if (pinState.connected) Fill else Stroke(width = 2f)
        )
    }
}

private object ExecDrawFunction : PinDrawFunction {

    override fun DrawScope.onDraw(pinState: PinState) {
        val path = Path()

        val center = size.center

        path.moveTo(0f, 0f)
        path.lineTo(center.x, 0f)
        path.lineTo(size.width, center.y)
        path.lineTo(center.x, size.height)
        path.lineTo(0f, size.height)
        path.lineTo(0f, 0f)

        val style: DrawStyle = if (pinState.connected) Fill else Stroke(width = 1.3f)

        drawPath(
            path = path,
            // white
            color = Color(pinState.pinRepresentation.color),
            style = style
        )
    }

}
