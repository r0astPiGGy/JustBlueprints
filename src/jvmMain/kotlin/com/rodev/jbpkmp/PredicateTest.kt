package com.rodev.jbpkmp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.rodev.jbpkmp.presentation.components.Sheet
import com.rodev.jbpkmp.presentation.components.validator.*
import com.rodev.jbpkmp.presentation.localization.*
import com.rodev.jbpkmp.theme.AppTheme

fun main() = singleWindowApplication(
    title = appName,
    state = WindowState(placement = WindowPlacement.Maximized)
) {
        AppTheme(useDarkTheme = true) {

            Surface {

                TestScreen()
            }
        }
}

@Composable
private fun TestScreen() {
    Box {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            var dialogPresented by remember { mutableStateOf(false) }
            var variableName: String? by remember { mutableStateOf(null) }

            Button(
                onClick = {
                    dialogPresented = true
                }
            ) {
                Text("Dialog")
            }

            variableName?.let {
                Text(it)
            }

            CreateDialog(
                presented = dialogPresented,
                onDismiss = { dialogPresented = false },
                onResult = { variableName = it }
            )
        }
    }
}

@Composable
private fun CreateDialog(
    presented: Boolean,
    onDismiss: () -> Unit,
    onResult: (String) -> Unit
) {
    Sheet(
        presented = presented,
        onDismissRequest = onDismiss
    ) {
        var variableName by remember { mutableStateOf("") }

        Card(
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .background(MaterialTheme.colors.surface)
                    .padding(10.dp)
            ) {
                val validator = rememberValidatorGroup()

                ValidatableTextField(
                    text = variableName,
                    onTextChanged = { variableName = it },
                    placeholder = { Text("Variable name") },
                    state = validator.rememberNew(
                        compoundValidatorOf(
                            Validators.textNotEmpty { "Variable name cannot be empty!" },
                            Validators.textMax(30) { "Name must be < 30 characters long" }
                        )
                    )
                )

                Spacer(Modifier.height(15.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val buttonWidth = 100.dp

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.width(buttonWidth),
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = MaterialTheme.colors.surface
                        )
                    ) {
                        Text(Vocabulary.localization.cancel())
                    }

                    Button(
                        onClick = {
                            if (validator.success) {
                                onDismiss()
                                onResult(variableName)
                            }
                        },
                        enabled = validator.success,
                        modifier = Modifier.width(buttonWidth)
                    ) {
                        Text(Vocabulary.localization.create())
                    }
                }
            }
        }
    }
}