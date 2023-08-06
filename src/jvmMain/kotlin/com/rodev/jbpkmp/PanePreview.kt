package com.rodev.jbpkmp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.application

fun main() = application {
    Surface(
        color = Color.Black
    ) {
        // Main screen
        Row {
            // Left Pane
            // First split
            Column {

                // Overview
                Column {

                }

                // Bottom Pane
                // Details
                Column {

                }

            }

            // Second split
            Column {
                Row {
                    // ViewPort
                    Box(modifier = Modifier)
                    // Right Pane
                    Column {

                    }
                }
                // Bottom Pane
                Column {

                }
            }
        }
    }
}

@Composable
fun SplitPane() {

}