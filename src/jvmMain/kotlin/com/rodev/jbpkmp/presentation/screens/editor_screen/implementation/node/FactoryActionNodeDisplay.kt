package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rodev.generator.action.entity.ActionDetails
import com.rodev.jbpkmp.domain.model.NodeEntity
import com.rodev.jbpkmp.domain.source.IconDataSource
import com.rodev.jbpkmp.presentation.components.MaterialCheckbox
import com.rodev.jbpkmp.presentation.localization.Vocabulary
import com.rodev.jbpkmp.presentation.localization.hideArguments
import com.rodev.jbpkmp.presentation.screens.editor_screen.SelectionHandler
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.FactoryNode
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.NodeDetailsPanel
import com.rodev.jbpkmp.presentation.screens.editor_screen.inheritBuilder
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.model.Node

class FactoryActionNodeDisplay(
    private val nodeEntity: NodeEntity,
    selectionHandler: SelectionHandler,
    private val iconDataSource: IconDataSource,
    private val actionDetails: ActionDetails?,
    hiddenInitially: Boolean = false
) : DefaultNodeDisplay(
    nodeEntity,
    selectionHandler,
    iconDataSource,
    actionDetails
) {

    private var inputHidden by mutableStateOf(hiddenInitially)

    @Composable
    override fun NodeView(nodeState: NodeState) {
        FactoryNode(
            nodeState = nodeState,
            nodeEntity = nodeEntity,
            selected = selected,
            onTap = { onSelect(nodeState) },
            inputHidden = inputHidden,
            iconDataSource = iconDataSource
        )
    }

    @Composable
    override fun Details(nodeState: NodeState) {
        actionDetails?.let {
            NodeDetailsPanel(it)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = hideArguments(Vocabulary.localization))
            MaterialCheckbox(
                checked = inputHidden,
                onCheckedChange = { inputHidden = it },
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        nodeState.inputPins.forEach {
            Text(text = it.pinState.pinDisplay.name)
            it.pinState.defaultValueComposable.DefaultValueView(it.pinState)
            Spacer(modifier = Modifier.size(8.dp))
        }
    }

    override fun toNode(nodeState: NodeState): Node {
        val node = super.toNode(nodeState)

        return node.copy(
            tag = node.tag.inheritBuilder {
                putBoolean(HIDDEN_TAG, inputHidden)
            }
        )
    }

    companion object {
        const val HIDDEN_TAG = "hidden"
    }

}