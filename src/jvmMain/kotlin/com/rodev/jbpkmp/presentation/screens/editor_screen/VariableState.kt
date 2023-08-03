package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
