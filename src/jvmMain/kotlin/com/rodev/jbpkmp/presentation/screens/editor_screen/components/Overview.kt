package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.rodev.jbpkmp.domain.model.variable.GlobalVariable
import com.rodev.jbpkmp.presentation.localization.Vocabulary
import com.rodev.jbpkmp.presentation.localization.localVariables
import com.rodev.jbpkmp.presentation.localization.globalVariables
import com.rodev.jbpkmp.presentation.localization.name
import com.rodev.jbpkmp.presentation.screens.editor_screen.*

@Composable
fun Overview(
    modifier: Modifier = Modifier,
    viewModel: EditorScreenViewModel
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colors.background)
    ) {
        LocalVariables(viewModel)
        GlobalVariables(viewModel)
    }
}

@Composable
private fun LocalVariables(
    viewModel: EditorScreenViewModel
) {
    val localization = Vocabulary.localization
    val currentGraph = viewModel.currentGraph
    var createVariableDialogPresented by remember { mutableStateOf(false) }

    CollapsibleList(
        header = localization.localVariables(),
        onAddAction = {
            createVariableDialogPresented = true
        }
    ) {
        if (currentGraph != null) {
            items(currentGraph.variables) {
                VariableView(it)
            }
        }
    }

    if (createVariableDialogPresented) {
        CreateVariableDialog { name, value ->
            if (name != null) {
                EditorScreenEvent.AddLocalVariable(
                    LocalVariableState(
                        name = name,
                        value = value
                    )
                ).let(viewModel::onEvent)
            }

            createVariableDialogPresented = false
        }
    }
}

@Composable
fun VariableView(variable: VariableState) {
    DragTarget(
        dataToDrop = variable
    ) {
        Text(variable.name)
    }
}

@Composable
private fun GlobalVariables(
    viewModel: EditorScreenViewModel
) {
    val localization = Vocabulary.localization
    var createVariableDialogPresented by remember { mutableStateOf(false) }

    CollapsibleList(
        header = localization.globalVariables(),
        onAddAction = {
            createVariableDialogPresented = true
        }
    ) {
        items(viewModel.state.variables) {
            VariableView(it)
        }
    }

    if (createVariableDialogPresented) {
        CreateVariableDialog { name, value ->
            if (name != null) {
                EditorScreenEvent.AddGlobalVariable(
                    GlobalVariableState(
                        name = name,
                        value = value,
                        // TODO
                        type = GlobalVariable.Type.GAME
                    )
                ).let(viewModel::onEvent)
            }
            createVariableDialogPresented = false
        }
    }
}

@Composable
fun CollapsibleList(
    header: String,
    onAddAction: () -> Unit = {},
    content: LazyListScope.() -> Unit
) {
    var visible by remember { mutableStateOf(true) }
    val rotation by animateFloatAsState(
        targetValue = if (visible) 0F else -90F
    )

    Column {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(5.dp))
                .clickable { visible = !visible }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.rotate(rotation)
            )

            Text(text = header)

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onAddAction) {
                    Icon(Icons.Default.AddCircle, contentDescription = null)
                }
            }
        }

        AnimatedVisibility(visible) {
            LazyColumn(
                modifier = Modifier.padding(start = 15.dp),
                content = content
            )
        }
    }
}
