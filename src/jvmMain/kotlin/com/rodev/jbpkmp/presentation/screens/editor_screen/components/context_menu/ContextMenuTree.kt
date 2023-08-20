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

sealed class ContextTreeNode {
    abstract val name: String

    internal var mutableVisibility by mutableStateOf(true)
    val visible: Boolean
        get() = mutableVisibility

    class Root(
        val child: List<ContextTreeNode> = emptyList(),
        override val name: String,
    ) : ContextTreeNode() {
        var expanded by mutableStateOf(false)
    }

    class Leaf(
        override val name: String,
        val id: String,
        private val iconProvider: IconProvider,
        val tooltipComposable: TooltipComposable? = null
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

typealias TooltipComposable = @Composable () -> Unit

fun ContextTreeNode.updateVisibility(
    onEachRoot: ContextTreeNode.Root.(matchesPredicate: Boolean) -> Unit = {},
    predicate: (ContextTreeNode.Leaf) -> Boolean
): Boolean {
    when (this) {
        is ContextTreeNode.Leaf -> {
            val result = predicate(this)
            mutableVisibility = result
            return result
        }

        is ContextTreeNode.Root -> {
            var result = false
            child.forEach {
                val iterationResult = it.updateVisibility(onEachRoot, predicate)

                if (iterationResult) {
                    result = true
                }
            }
            onEachRoot(result)

            mutableVisibility = result

            return result
        }
    }
}