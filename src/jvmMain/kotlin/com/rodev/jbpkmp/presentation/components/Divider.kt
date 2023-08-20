package com.rodev.jbpkmp.presentation.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

const val dividerSize = 1

@Composable
fun VerticalDivider(color: Color = Color.Black) {
    Divider(
        modifier = Modifier
            .fillMaxHeight()
            .width(dividerSize.dp),
        color = color
    )
}

@Composable
fun HorizontalDivider(color: Color = Color.Black) {
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .height(dividerSize.dp),
        color = color
    )
}