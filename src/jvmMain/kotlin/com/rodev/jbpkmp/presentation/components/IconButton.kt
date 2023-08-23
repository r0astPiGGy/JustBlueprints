package com.rodev.jbpkmp.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp

@Composable
fun MaterialIconButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    imageVector: ImageVector,
    onClick: () -> Unit = {},
) {
    MaterialIconButton(
        modifier,
        enabled,
        painter = rememberVectorPainter(imageVector),
        onClick
    )
}


@Composable
fun MaterialIconButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    painter: Painter,
    onClick: () -> Unit = {},
) {
    IconButton(
        enabled = enabled,
        onClick = onClick,
        modifier = modifier
            .alpha(if (enabled) 1f else 0.5f)
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painter,
            contentDescription = null,
            tint = MaterialTheme.colors.primary
        )
    }
}