package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rodev.jbpkmp.domain.model.variable.GlobalVariable
import com.rodev.jbpkmp.domain.model.variable.LocalVariable
import com.rodev.jbpkmp.presentation.localization.name
import java.util.UUID

sealed interface VariableState : Selectable {

    val id: String
    var name: String
    var value: Any?

}

class LocalVariableState(
    override val id: String = UUID.randomUUID().toString(),
    name: String,
    value: Any? = null
) : VariableState {

    override var selected: Boolean by mutableStateOf(false)
    override var name by mutableStateOf(name)

    override var value by mutableStateOf(value)

    override fun onDelete(selectionActionVisitor: SelectionActionVisitor) {
        selectionActionVisitor.deleteLocalVariable(this)
    }

    @Composable
    override fun Details() {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Имя переменной") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = value.toString(),
            onValueChange = { value = it },
            label = { Text("Дефолтное значение") },
            modifier = Modifier.fillMaxWidth()
        )
    }

}

class GlobalVariableState(
    override val id: String = UUID.randomUUID().toString(),
    name: String,
    value: Any? = null,
    type: GlobalVariable.Type
) : VariableState {

    override var selected: Boolean by mutableStateOf(false)
    override var name by mutableStateOf(name)

    override var value by mutableStateOf(value)
    var type by mutableStateOf(type)

    override fun onDelete(selectionActionVisitor: SelectionActionVisitor) {
        selectionActionVisitor.deleteGlobalVariable(this)
    }

    @Composable
    override fun Details() {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Имя переменной") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = value.toString(),
            onValueChange = { value = it },
            label = { Text("Дефолтное значение") },
            modifier = Modifier.fillMaxWidth()
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
        name = name,
        value = value?.toString()
    )
}

fun LocalVariable.toState(): LocalVariableState {
    return LocalVariableState(
        id = id,
        name = name,
        value = value
    )
}

fun GlobalVariableState.toGlobalVariable(): GlobalVariable {
    return GlobalVariable(
        type = type,
        id = id,
        name = name,
        value = value?.toString()
    )
}

fun GlobalVariable.toState(): GlobalVariableState {
    return GlobalVariableState(
        id = id,
        name = name,
        value = value,
        type = type
    )
}
