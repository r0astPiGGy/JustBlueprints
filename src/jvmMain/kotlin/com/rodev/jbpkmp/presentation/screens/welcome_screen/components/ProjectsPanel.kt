package com.rodev.jbpkmp.presentation.screens.welcome_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rodev.jbpkmp.presentation.localization.Vocabulary
import com.rodev.jbpkmp.presentation.localization.noRecentProjects
import com.rodev.jbpkmp.presentation.screens.welcome_screen.ProjectsPanelState

@Composable
fun ProjectsPanel(
    modifier: Modifier = Modifier,
    state: ProjectsPanelState
) {
    val localization = Vocabulary.localization

    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(MaterialTheme.colors.background)
    ) {
        if (state.projects.isEmpty()) {
            Text(
                text = localization.noRecentProjects(),
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .align(Alignment.Center)
            )
        }
        LazyColumn(
            modifier = modifier
                .fillMaxHeight()
        ) {
            items(state.projects) { projectState ->
                RecentProjectItem(
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(5.dp))
                        .background(
                            if (projectState.selected) {
                                MaterialTheme.colors.primary
                            } else {
                                Color.Transparent
                            }
                        )
                        .clickable { state.onSelect(projectState) },
                    state = projectState,
                    onDeleteClick = {
                        state.onDelete(projectState)
                    }
                )
            }
        }
    }
}