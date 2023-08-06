package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ToolBar(
    modifier: Modifier = Modifier,
    startContent: @Composable () -> Unit,
    endContent: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colors.background)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row {
                startContent()
            }

            Row {
                endContent()
            }
        }
    }
}