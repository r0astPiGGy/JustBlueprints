package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rodev.jbpkmp.domain.model.variable.GlobalVariable
import com.rodev.jbpkmp.domain.model.variable.LocalVariable
import com.rodev.jbpkmp.presentation.localization.Vocabulary
import com.rodev.jbpkmp.presentation.localization.variableName
import java.util.UUID

sealed interface VariableState : Selectable {

    val id: String
    var name: String

}

class LocalVariableState(
    override val id: String = UUID.randomUUID().toString(),
    name: String
) : VariableState {

    override var selected: Boolean by mutableStateOf(false)
    override var name by mutableStateOf(name)

    override fun onDelete(selectionActionVisitor: SelectionActionVisitor) {
        selectionActionVisitor.deleteLocalVariable(this)
    }

    @Composable
    override fun Details() {
        val localization = Vocabulary.localization

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(localization.variableName()) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = MaterialTheme.colors.onBackground,
                unfocusedLabelColor = MaterialTheme.colors.onBackground,
                placeholderColor = MaterialTheme.colors.onBackground,
                textColor = MaterialTheme.colors.onBackground
            )
        )
    }

}

class GlobalVariableState(
    override val id: String = UUID.randomUUID().toString(),
    name: String,
    type: GlobalVariable.Type
) : VariableState {

    override var selected: Boolean by mutableStateOf(false)
    override var name by mutableStateOf(name)
    var type by mutableStateOf(type)

    override fun onDelete(selectionActionVisitor: SelectionActionVisitor) {
        selectionActionVisitor.deleteGlobalVariable(this)
    }

    @Composable
    override fun Details() {
        val localization = Vocabulary.localization

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(localization.variableName()) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = MaterialTheme.colors.onBackground,
                unfocusedLabelColor = MaterialTheme.colors.onBackground,
                placeholderColor = MaterialTheme.colors.onBackground,
                textColor = MaterialTheme.colors.onBackground
            )
        )

        Box {
            var expanded by remember { mutableStateOf(false) }

            OutlinedButton(
                onClick = { expanded = !expanded }
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(type.typeName)

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
            ) {
                GlobalVariable.Type.values().forEach {
                    DropdownMenuItem(
                        onClick = {
                            type = it
                            expanded = false
                        }
                    ) {
                        Text(it.typeName)
                    }
                }
            }
        }
    }
}

fun LocalVariableState.toLocalVariable(): LocalVariable {
    return LocalVariable(
        id = id,
        name = name
    )
}

fun LocalVariable.toState(): LocalVariableState {
    return LocalVariableState(
        id = id,
        name = name
    )
}

fun GlobalVariableState.toGlobalVariable(): GlobalVariable {
    return GlobalVariable(
        type = type,
        id = id,
        name = name
    )
}

fun GlobalVariable.toState(): GlobalVariableState {
    return GlobalVariableState(
        id = id,
        name = name,
        type = type
    )
}
