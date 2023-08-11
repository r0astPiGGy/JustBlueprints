package com.rodev.jbpkmp.presentation.screens.welcome_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.rodev.jbpkmp.domain.model.RecentProject

class RecentProjectState(
    val recentProject: RecentProject
) {

    var selected by mutableStateOf(false)

}