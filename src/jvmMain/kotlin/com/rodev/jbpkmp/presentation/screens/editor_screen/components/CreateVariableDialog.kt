package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rodev.jbpkmp.domain.model.changeable_variable.Variable
import com.rodev.jbpkmp.presentation.localization.Vocabulary
import com.rodev.jbpkmp.presentation.localization.cancel
import com.rodev.jbpkmp.presentation.localization.create
import com.rodev.jbpkmp.presentation.localization.errorMessage
import com.rodev.jbpkmp.presentation.localization.name
import com.rodev.jbpkmp.presentation.localization.value

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreateVariableDialog(onDismissRequest: (Variable?) -> Unit) {
    val localization = Vocabulary.localization

    var variableName by remember { mutableStateOf("") }
    var variableValue by remember { mutableStateOf("") }
    val isError = variableName.isEmpty() && variableName.isBlank()

    AlertDialog(
        onDismissRequest = { onDismissRequest(null) },
        buttons = {
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .background(MaterialTheme.colors.surface)
                    .padding(10.dp)
            ) {
                val errorModifier = Modifier.padding(top = 5.dp)

                OutlinedTextField(
                    value = variableName,
                    onValueChange = { variableName = it },
                    placeholder = { Text(localization.name()) },
                    singleLine = true,
                    isError = isError
                )

                if (isError) Text(
                    text = localization.errorMessage(),
                    modifier = errorModifier,
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.h5
                ) else {
                    Text(
                        text = "",
                        modifier = errorModifier,
                        style = MaterialTheme.typography.h5
                    )
                }

                Spacer(Modifier.height(15.dp))

                OutlinedTextField(
                    value = variableValue,
                    onValueChange = { variableValue = it },
                    placeholder = { Text(localization.value()) }
                )

                Spacer(Modifier.height(25.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val buttonWidth = 100.dp

                    OutlinedButton(
                        onClick = { onDismissRequest(null) },
                        modifier = Modifier.width(buttonWidth),
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = MaterialTheme.colors.surface
                        )
                    ) {
                        Text(localization.cancel())
                    }

                    Button(
                        onClick = {
                            if (!isError)
                                Variable(variableName, variableValue).let(onDismissRequest)
                        },
                        modifier = Modifier.width(buttonWidth)
                    ) {
                        Text(localization.create())
                    }
                }
            }
        }
    )
}