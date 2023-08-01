package com.rodev.jbpkmp.presentation.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

class NavController(
    private val startDestination: String,
    private val backStack: MutableSet<BackstackEntry> = mutableSetOf(),
    arguments: ArgumentBundle = emptyBundle()
) {
    var currentScreen by mutableStateOf(BackstackEntry(startDestination, arguments))

    fun navigate(route: String, args: ArgumentBundle = emptyBundle()) {
        if (route != currentScreen.route) {
            if (backStack.find { it.route == route } != null && currentScreen.route != startDestination) {
                removeEntry(currentScreen.route)
            }

            if (route == startDestination) {
                clearBackstack()
            } else {
                backStack.add(currentScreen)
            }

            currentScreen = BackstackEntry(route, args)
        }
    }

    private fun clearBackstack() {
        backStack.clear()
    }

    private fun removeEntry(route: String) {
        backStack.removeIf { it.route == route }
    }

    fun navigateBack() {
        if (backStack.isNotEmpty()) {
            currentScreen = backStack.last()
            backStack.removeIf { it.route == currentScreen.route }
        }
    }

    val args: ArgumentBundle
        get() = currentScreen.arguments
}

data class BackstackEntry(
    val route: String,
    val arguments: ArgumentBundle
)

interface ArgumentBundle {

    fun getString(id: String): String?

}

class MutableArgumentBundle: ArgumentBundle {

    private val values = hashMapOf<String, Any>()

    fun putString(id: String, string: String) {
        values[id] = string
    }

    override fun getString(id: String): String? {
        val value = values[id]

        if (value is String) {
            return value
        }

        return null
    }

}

fun emptyBundle(): ArgumentBundle = MutableArgumentBundle()

fun argumentBundleOf(builderScope: MutableArgumentBundle.() -> Unit): ArgumentBundle {
    return MutableArgumentBundle().apply(builderScope)
}

@Composable
fun rememberNavController(
    startDestination: String,
    arguments: ArgumentBundle = emptyBundle(),
    backStack: MutableSet<BackstackEntry> = mutableSetOf(),
): MutableState<NavController> = rememberSaveable {
    mutableStateOf(NavController(startDestination, backStack, arguments))
}