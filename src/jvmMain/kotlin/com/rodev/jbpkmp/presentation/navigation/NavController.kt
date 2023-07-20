package com.rodev.jbpkmp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable

class NavController(
    private val startDestination: String,
    private val backStack: MutableSet<String> = mutableSetOf()
) {
    var currentScreen = mutableStateOf(startDestination)

    fun navigate(route: String) {
        if (route != currentScreen.value) {
            if (backStack.contains(currentScreen.value) && currentScreen.value != startDestination) {
                backStack.remove(currentScreen.value)
            }

            if (route == startDestination) {
                backStack.clear()
            } else {
                backStack.add(currentScreen.value)
            }

            currentScreen.value = route
        }
    }

    fun navigateBack() {
        if (backStack.isNotEmpty()) {
            currentScreen.value = backStack.last()
            backStack.remove(currentScreen.value)
        }
    }
}

@Composable
fun rememberNavController(
    startDestination: String,
    backStack: MutableSet<String> = mutableSetOf()
): MutableState<NavController> = rememberSaveable {
    mutableStateOf(NavController(startDestination, backStack))
}