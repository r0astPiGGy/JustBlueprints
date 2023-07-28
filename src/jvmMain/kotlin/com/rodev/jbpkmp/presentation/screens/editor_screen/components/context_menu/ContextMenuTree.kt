package com.rodev.jbpkmp.presentation.screens.editor_screen.components.context_menu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

sealed class ContextTreeNode {
    abstract val name: String

    internal var mutableVisibility by mutableStateOf(true)
    val visible: Boolean
        get() = mutableVisibility

    class Root(
        val child: List<ContextTreeNode> = emptyList(),
        override val name: String
    ) : ContextTreeNode()

    class Leaf(
        override val name: String,
        val id: String,
        private val iconProvider: IconProvider
    ) : ContextTreeNode() {

        @get:Composable
        val icon: Painter
            get() {
                val icon = iconProvider() ?: return ColorPainter(Color.White)

                return BitmapPainter(icon)
            }

    }
}

typealias IconProvider = () -> ImageBitmap?

fun ContextTreeNode.updateVisibility(predicate: (ContextTreeNode.Leaf) -> Boolean): Boolean {
    when (this) {
        is ContextTreeNode.Leaf -> {
            val result = predicate(this)
            mutableVisibility = result
            return result
        }
        is ContextTreeNode.Root -> {
            var result = false
            child.forEach {
                val iterationResult = it.updateVisibility(predicate)

                if (iterationResult) {
                    result = true
                }
            }

            mutableVisibility = result

            return result
        }
    }
}