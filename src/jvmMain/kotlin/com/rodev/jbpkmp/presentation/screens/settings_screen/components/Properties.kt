package com.rodev.jbpkmp.presentation.screens.settings_screen.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BooleanProperty(
    modifier: Modifier = Modifier,
    value: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.h3
        )

        Checkbox(
            checked = value,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colors.primary)
        )
    }
}

@Composable
fun EnumProperty(
    modifier: Modifier = Modifier,
    state: EnumPropertyState = rememberEnumPropertyState(),
    label: String,
    propertyText: String,
    dropdownContent: @Composable (state: EnumPropertyState) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Text(label)

        Box {
            val width = 200.dp

            OutlinedButton(
                onClick = { state.expanded = !state.expanded },
                modifier = Modifier.width(width)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(propertyText)

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            }

            DropdownMenu(
                expanded = state.expanded,
                onDismissRequest = { state.expanded = false },
                modifier = Modifier.width(width)
            ) {
                dropdownContent(state)
            }
        }
    }
}

@Composable
fun rememberEnumPropertyState() = remember { EnumPropertyState() }

class EnumPropertyState {

    var expanded by mutableStateOf(false)

}