package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rodev.generator.action.entity.extra_data.EnumEntry
import com.rodev.generator.action.utils.toMap
import com.rodev.jbpkmp.domain.model.Selector
import com.rodev.jbpkmp.domain.model.SelectorGroup
import com.rodev.nodeui.components.pin.DefaultValueComposable
import com.rodev.nodeui.components.pin.PinState

class StringInputComposable : DefaultValueComposable {

    private var input by mutableStateOf("")

    @Composable
    override fun DefaultValueView(pinState: PinState) {
        TextField(input, onValueChange = { input = it }, modifier = Modifier.fillMaxWidth() )
    }

    override fun getValue(): String {
        return input
    }

    override fun setValue(any: String?) {
        input = any ?: ""
    }

}

class SelectorInputComposable(
    private val selectors: List<Selector>
) : DefaultValueComposable {

    private val entriesById = selectors.toMap { it.id }
    private var input by mutableStateOf(selectors.first())

    @Composable
    override fun DefaultValueView(pinState: PinState) {
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
                    Text(input.name)

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
                selectors.forEach {
                    DropdownMenuItem(
                        onClick = {
                            input = it
                            expanded = false
                        }
                    ) {
                        Text(it.name)
                    }
                }
            }
        }
    }

    override fun getValue(): String = input.id

    override fun setValue(any: String?) {
        if (any == null) return

        entriesById[any]?.let { input = it }
    }

}

class EnumInputComposable(
    private val enumEntries: List<EnumEntry>
) : DefaultValueComposable {

    private val entriesById = enumEntries.toMap { it.id }
    private var input by mutableStateOf(enumEntries.first())

    @Composable
    override fun DefaultValueView(pinState: PinState) {
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
                    Text(input.name)

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
                enumEntries.forEach {
                    DropdownMenuItem(
                        onClick = {
                            input = it
                            expanded = false
                        }
                    ) {
                        Text(it.name)
                    }
                }
            }
        }
    }

    override fun getValue(): String = input.id

    override fun setValue(any: String?) {
        if (any == null) return

        entriesById[any]?.let { input = it }
    }

}

class BooleanInputComposable : DefaultValueComposable {

    private var input by mutableStateOf(false)

    @Composable
    override fun DefaultValueView(pinState: PinState) {
        Checkbox(checked = input, onCheckedChange = { input = it }, modifier = Modifier.size(20.dp))
    }

    override fun getValue(): String = input.toString()

    override fun setValue(any: String?) {
        any?.toBoolean()?.let { input = it }
    }

}