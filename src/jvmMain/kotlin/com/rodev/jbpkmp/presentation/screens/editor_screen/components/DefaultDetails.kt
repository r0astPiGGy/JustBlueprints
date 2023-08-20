package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.rodev.generator.action.entity.ActionDetails
import com.rodev.jbpkmp.presentation.localization.Vocabulary
import com.rodev.jbpkmp.presentation.localization.additionally
import com.rodev.jbpkmp.presentation.localization.description
import com.rodev.jbpkmp.presentation.localization.worksWith

@Composable
fun NodeDetailsPanel(details: ActionDetails) {
    Text(
        text = details.name,
        fontStyle = FontStyle.Italic,
        color = MaterialTheme.colors.onBackground
    )

    Spacer(modifier = Modifier.size(8.dp))

    DetailsPanel(details)
}

@Composable
fun DetailsPanel(details: ActionDetails) {
    val localization = Vocabulary.localization

    details.description?.let {
        Text(
            text = localization.description(),
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colors.onBackground
        )

        Spacer(modifier = Modifier.size(5.dp))

        Text(
            text = it,
            style = MaterialTheme.typography.h5,
            color = MaterialTheme.colors.onBackground
        )

        Spacer(modifier = Modifier.size(8.dp))
    }

    if (details.additionalInfo.isNotEmpty()) {
        Text(
            text = localization.additionally(),
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colors.onBackground
        )

        Spacer(modifier = Modifier.size(5.dp))

        details.additionalInfo.forEach {
            Text(
                text = it,
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.onBackground
            )

            Spacer(modifier = Modifier.size(5.dp))
        }

        Spacer(modifier = Modifier.size(3.dp))
    }

    if (details.worksWith.isNotEmpty()) {
        Text(
            text = localization.worksWith(),
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colors.onBackground
        )

        Spacer(modifier = Modifier.size(5.dp))

        details.worksWith.forEach {
            Text(
                text = it,
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.onBackground
            )

            Spacer(modifier = Modifier.size(5.dp))
        }
    }
}