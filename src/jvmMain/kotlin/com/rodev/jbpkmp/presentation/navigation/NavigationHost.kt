package com.rodev.jbpkmp.presentation.navigation

import androidx.compose.runtime.Composable

class NavigationHost(
    val navController: NavController,
    val content: @Composable NavigationGraphBuilder.() -> Unit
) {
    @Composable
    fun build() {
        NavigationGraphBuilder().renderContent()
    }

    inner class NavigationGraphBuilder(
        val navController: NavController = this@NavigationHost.navController
    ) {
        @Composable
        fun renderContent() {
            this@NavigationHost.content(this)
        }
    }
}

@Composable
fun NavigationHost.NavigationGraphBuilder.composable(
    route: String,
    content: @Composable ArgumentBundle.() -> Unit
) {
    if (navController.currentScreen.route == route) {
        content(navController.args)
    }
}