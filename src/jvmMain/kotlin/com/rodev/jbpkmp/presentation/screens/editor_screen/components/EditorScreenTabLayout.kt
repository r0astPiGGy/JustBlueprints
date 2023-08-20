package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rodev.jbpkmp.presentation.components.HorizontalDivider
import com.rodev.jbpkmp.presentation.components.Tab
import com.rodev.jbpkmp.presentation.components.TabLayout
import com.rodev.jbpkmp.presentation.components.TabLayoutHostState
import com.rodev.jbpkmp.presentation.screens.editor_screen.GraphState
import com.rodev.jbpkmp.presentation.screens.editor_screen.InvokableState
import kotlin.math.PI
import kotlin.math.sin

class InvokableTab(
    private val invokableState: InvokableState
) : Tab<GraphState> {

    override val closeable: Boolean = true
    override val name: String by derivedStateOf {
        invokableState.name
    }
    override var selected: Boolean by mutableStateOf(false)
    override val data: GraphState
        get() = invokableState.graphState

}

@Composable
fun <T> EditorScreenTabLayout(
    modifier: Modifier = Modifier,
    state: TabLayoutHostState<T> = remember { TabLayoutHostState() },
    content: @Composable (Tab<T>?) -> Unit
) {
    TabLayout(
        modifier = modifier,
        state = state,
        tabs = {
            EditorTabList(
                modifier = Modifier
                    .height(38.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.background),
                state = state,
                onTabClick = it::onTabSelected,
                onTabClose = it::onTabClosed
            )
            HorizontalDivider()
        },
        content = content
    )
}

@Composable
private fun <T> EditorTabList(
    modifier: Modifier = Modifier,
    state: TabLayoutHostState<T>,
    onTabClick: (Tab<T>) -> Unit,
    onTabClose: (Tab<T>) -> Unit
) {
    LazyRow(
        modifier = modifier
    ) {
        items(state.tabs) {
            EditorTab(
                state = it,
                onClick = { onTabClick(it) },
                onClose = { onTabClose(it) }
            )
        }
    }
}

private const val indicatorHeight = 3
private const val tabPadding = 10
private const val tabBottomPadding = tabPadding - indicatorHeight
private const val crossSpacing = 6
private const val crossSize = 18
private const val hiddenCrossSize = crossSize + crossSpacing

@Composable
private fun <T> EditorTab(
    modifier: Modifier = Modifier,
    state: Tab<T>,
    onClick: () -> Unit,
    onClose: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val displayCrossButton by derivedStateOf {
        hovered && state.closeable || state.closeable && state.selected
    }

    Column(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .alpha(if (hovered || state.selected) 1f else 0.7f)
            .width(IntrinsicSize.Max)
    ) {
        Row(
            modifier = Modifier.padding(
                start = tabPadding.dp,
                top = tabPadding.dp,
                end = tabPadding.dp,
                bottom = tabBottomPadding.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = state.name,
                fontSize = MaterialTheme.typography.h5.fontSize,
                modifier = Modifier.alignByBaseline(),
                maxLines = 1
            )
            if (displayCrossButton) {
                Spacer(Modifier.width(crossSpacing.dp))
                CloseButton(
                    buttonSize = crossSize.dp,
                    background = Color.Transparent,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = CloseButtonIndication,
                            onClick = onClose
                        )
                        .alignByBaseline()
                )
            } else if (state.closeable) {
                Spacer(Modifier.width(hiddenCrossSize.dp))
            }
        }
        Spacer(Modifier.weight(1f))
        Divider(
            thickness = indicatorHeight.dp,
            color = if (state.selected) MaterialTheme.colors.primary else MaterialTheme.colors.background
        )
    }
}

private object CloseButtonIndication : Indication {

    private class CloseButtonIndicationInstance(
        private val isHovered: State<Boolean>
    ) : IndicationInstance {
        override fun ContentDrawScope.drawIndication() {
            drawContent()
            if (isHovered.value) {
                drawCircle(color = Color.White.copy(alpha = 0.2f))
            }
        }
    }

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        val isHovered = interactionSource.collectIsHoveredAsState()
        return remember(interactionSource) {
            CloseButtonIndicationInstance(isHovered)
        }
    }
}

private val sin45 = sin(PI / 4)
private val crossPadding = 0.65f

@Composable
@Preview
private fun CloseButton(
    modifier: Modifier = Modifier,
    buttonSize: Dp,
    background: Color = Color.Black,
    lineColor: Color = Color.White,
) {
    Spacer(modifier.size(buttonSize).clip(CircleShape).drawWithCache {
        val halfHeight = size.height / 2f
        val rectTop = halfHeight - ( sin45 * (size.height / 2) * crossPadding ).toFloat()

        onDrawBehind {
            drawRect(background)
            inset(
                left = rectTop,
                top = rectTop,
                right = rectTop,
                bottom = rectTop
            ) {
                drawLine(Color.White, Offset.Zero, Offset(size.width, size.height))
                drawLine(Color.White, Offset(size.width, 0f), Offset(0f, size.height))
            }
        }
    })
}

@Preview
@Composable
fun CloseButtonPreview() {
    Surface(
        color = Color.White
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CloseButton(
                buttonSize = 40.dp,
            )
        }
    }
}
