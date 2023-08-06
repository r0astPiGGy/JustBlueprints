package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.rodev.generator.action.entity.ActionDetails
import com.rodev.generator.action.entity.extra_data.CompoundExtraData
import com.rodev.generator.action.entity.extra_data.ConnectionDisabledExtraData
import com.rodev.generator.action.entity.extra_data.ExecPairExtraData
import com.rodev.generator.action.entity.extra_data.ExtraData
import com.rodev.jbpkmp.domain.model.NodeEntity
import com.rodev.jbpkmp.presentation.localization.Vocabulary
import com.rodev.jbpkmp.presentation.localization.additionally
import com.rodev.jbpkmp.presentation.localization.description
import com.rodev.jbpkmp.presentation.localization.worksWith
import com.rodev.jbpkmp.presentation.screens.editor_screen.SelectionHandler
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.StyledNode
import com.rodev.jbpkmp.presentation.screens.editor_screen.createNodeTypeTag
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.pin.extra
import com.rodev.nodeui.components.node.NodeDisplay
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.components.pin.PinState
import com.rodev.nodeui.components.pin.row.PinRowState
import com.rodev.nodeui.model.Node

class DefaultNodeDisplay(
    private val nodeEntity: NodeEntity,
    private val selectionHandler: SelectionHandler,
    private val actionDetails: ActionDetails?
) : NodeDisplay {

    private var selected: Boolean by mutableStateOf(false)

    @Composable
    override fun NodeView(nodeState: NodeState) {
        val specificNodePins = rememberSpecificNodePins(nodeState)

        StyledNode(specificNodePins,
            nodeState = nodeState,
            nodeEntity = nodeEntity,
            selected = selected,
            onTap = { onSelect(nodeState) })
    }

    @Composable
    private fun Details() {
        val localization = Vocabulary.localization

        actionDetails?.let { details ->
            Text(
                text = details.name,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colors.onBackground
            )

            Spacer(modifier = Modifier.size(8.dp))

            details.description?.let {
                Text(
                    text = localization.description(),
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colors.onBackground
                )

                Spacer(modifier = Modifier.size(5.dp))

                Text(
                    text = it,
                    style = MaterialTheme.typography.h5,
                    color = MaterialTheme.colors.onBackground
                )

                Spacer(modifier = Modifier.size(8.dp))
            }

            if (details.additionalInfo.isNotEmpty()) {
                Text(
                    text = localization.additionally(),
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colors.onBackground
                )

                Spacer(modifier = Modifier.size(5.dp))

                details.additionalInfo.forEach {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.h5,
                        color = MaterialTheme.colors.onBackground
                    )

                    Spacer(modifier = Modifier.size(5.dp))
                }

                Spacer(modifier = Modifier.size(3.dp))
            }

            if (details.worksWith.isNotEmpty()) {
                Text(
                    text = localization.worksWith(),
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colors.onBackground
                )

                Spacer(modifier = Modifier.size(5.dp))

                details.worksWith.forEach {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.h5,
                        color = MaterialTheme.colors.onBackground
                    )

                    Spacer(modifier = Modifier.size(5.dp))
                }
            }
        }
    }

    private fun onSelect(nodeState: NodeState) {
        if (selected) {
            selectionHandler.resetSelection()
            return
        }
        selectionHandler.onSelect(
            NodeStateSelectableWrapper(selectGetter = { selected },
                selectSetter = { selected = it },
                nodeState = nodeState,
                detailsComposable = { Details() })
        )
    }

    override fun toNode(nodeState: NodeState): Node {
        return Node(x = nodeState.x,
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