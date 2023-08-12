package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rodev.jbpkmp.presentation.screens.editor_screen.EditorScreenViewModel

@Composable
fun DetailsPanel(
    modifier: Modifier = Modifier,
    viewModel: EditorScreenViewModel
) {

    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colors.background)
            .then(modifier)
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(12.dp)
                .verticalScroll(state = scrollState),
        ) {
            viewModel.selectable?.Details()
        }

        VerticalScrollbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = scrollState
            )
        )
    }
}