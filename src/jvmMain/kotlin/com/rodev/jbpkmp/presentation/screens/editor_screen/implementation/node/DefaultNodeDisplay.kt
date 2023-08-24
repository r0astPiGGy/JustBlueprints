package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node

import androidx.compose.runtime.*
import com.rodev.generator.action.entity.ActionDetails
import com.rodev.generator.action.entity.extra_data.CompoundExtraData
import com.rodev.generator.action.entity.extra_data.ConnectionDisabledExtraData
import com.rodev.generator.action.entity.extra_data.ExecPairExtraData
import com.rodev.generator.action.entity.extra_data.ExtraData
import com.rodev.jbpkmp.domain.model.NodeEntity
import com.rodev.jbpkmp.domain.source.IconDataSource
import com.rodev.jbpkmp.presentation.screens.editor_screen.SelectionHandler
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.DetailsPanel
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.NodeDetailsPanel
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.StyledNode
import com.rodev.jbpkmp.presentation.screens.editor_screen.createNodeTypeTag
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.pin.extra
import com.rodev.jbpkmp.util.generateUniqueId
import com.rodev.nodeui.components.node.NodeDisplay
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.components.pin.PinState
import com.rodev.nodeui.components.pin.row.PinRowState
import com.rodev.nodeui.model.Node

open class DefaultNodeDisplay(
    private val nodeEntity: NodeEntity,
    private val selectionHandler: SelectionHandler,
    private val iconDataSource: IconDataSource,
    private val actionDetails: ActionDetails?,
    private val copyEnabled: Boolean = true,
    private val deletionEnabled: Boolean = true
) : NodeDisplay {

    var selected: Boolean by mutableStateOf(false)

    @Composable
    override fun NodeView(nodeState: NodeState) {
        StyledNode(
            specificNodePins = rememberSpecificNodePins(nodeState),
            nodeState = nodeState,
            nodeEntity = nodeEntity,
            selected = selected,
            onTap = { onSelect(nodeState) },
            iconDataSource = iconDataSource
        )
    }

    @Composable
    protected open fun Details(nodeState: NodeState) {
        actionDetails?.let {
            NodeDetailsPanel(it)
        }
    }

    fun onSelect(nodeState: NodeState) {
        if (selected) {
            selectionHandler.resetSelection()
            return
        }
        selectionHandler.onSelect(
            NodeStateSelectableWrapper(
                selectGetter = { selected },
                selectSetter = { selected = it },
                nodeState = nodeState,
                nodeSupplier = { copyToNode(nodeState) },
                detailsComposable = { Details(nodeState) },
                copyEnabled = copyEnabled,
                deletionEnabled = deletionEnabled
            )
        )
    }

    private fun copyToNode(nodeState: NodeState): Node {
        return toNode(nodeState).copy(
            uniqueId = generateUniqueId(),
            inputPins = nodeState.inputPins.map { it.pinState }.map {
                it.pinDisplay
                    .toPin(it)
                    .copy(uniqueId = generateUniqueId())
            },
            outputPins = nodeState.outputPins.map { it.pinState }.map {
                it.pinDisplay
                    .toPin(it)
                    .copy(uniqueId = generateUniqueId())
            },
        )
    }

    override fun toNode(nodeState: NodeState): Node {
        return Node(
            x = nodeState.x,
            y = nodeState.y,
            uniqueId = nodeState.id,
            inputPins = nodeState.inputPins.map { it.pinState }.map { it.pinDisplay.toPin(it) },
            outputPins = nodeState.outputPins.map { it.pinState }.map { it.pinDisplay.toPin(it) },
            tag = createNodeTypeTag(typeId = nodeEntity.id)
        )
    }
}

@Composable
fun rememberSpecificNodePins(nodeState: NodeState): SpecificNodePins {
    return remember(nodeState.inputPins, nodeState.outputPins) {
        findSpecificNodePins(nodeState)
    }
}

private fun findSpecificNodePins(nodeState: NodeState): SpecificNodePins {
    var inputExec: PinRowState? = null
    var outputExec: PinRowState? = null

    val inputPins = mutableListOf<PinRowState>()
    val outputPins = mutableListOf<PinRowState>()
    val connectionDisabledInputPins = mutableListOf<PinRowState>()

    var execPinPair: ExecPinPair? = null

    nodeState.inputPins.forEach {
        when {
            it.isExecPair() && inputExec == null -> {
                inputExec = it
            }

            it.isConnectionDisabled() -> {
                connectionDisabledInputPins.add(it)
            }

            else -> {
                inputPins.add(it)
            }
        }
    }

    nodeState.outputPins.forEach {
        if (it.isExecPair() && outputExec == null) {
            outputExec = it
        } else {
            outputPins.add(it)
        }
    }

    if (inputExec != null && outputExec != null) {
        execPinPair = ExecPinPair(inputExec!!, outputExec!!)
    } else {
        inputExec?.let { inputPins.add(0, it) }
        outputExec?.let { outputPins.add(0, it) }
    }

    return SpecificNodePins(
        inputPins, outputPins, connectionDisabledInputPins, execPinPair
    )
}

private fun PinRowState.isExecPair(): Boolean {
    return pinState.extraData.containsExtraData<ExecPairExtraData>()
}

private fun PinRowState.isConnectionDisabled(): Boolean {
    return pinState.extraData.containsExtraData<ConnectionDisabledExtraData>()
}

private val PinState.extraData: ExtraData?
    get() = this.pinDisplay.extra

private inline fun <reified T : ExtraData> ExtraData?.containsExtraData(): Boolean {
    if (this is CompoundExtraData) {
        return this.containsExtraDataOfType<T>()
    }

    return this is T
}

class SpecificNodePins(
    val inputPins: List<PinRowState>,
    val outputPins: List<PinRowState>,
    val connectionDisabledInputPins: List<PinRowState>,
    val execPinPair: ExecPinPair? = null
)

class ExecPinPair(
    val input: PinRowState, val output: PinRowState
)