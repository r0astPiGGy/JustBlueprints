package com.rodev.jbpkmp

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
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

//            CreateDialog(
//                presented = dialogPresented,
//                onDismiss = { dialogPresented = false },
//                onResult = { variableName = it }
//            )
        }
    }
}