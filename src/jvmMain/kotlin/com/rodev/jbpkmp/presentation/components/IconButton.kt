package com.rodev.jbpkmp.presentation.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun MaterialIconButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    imageVector: ImageVector,
    onClick: () -> Unit = {},
) {
    IconButton(
        enabled = enabled,
        onClick = onClick,
        modifier = modifier
            .alpha(if (enabled) 1f else 0.5f)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = MaterialTheme.colors.primary
        )
    }
}