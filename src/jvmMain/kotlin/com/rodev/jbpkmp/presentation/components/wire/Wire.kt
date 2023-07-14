package com.rodev.jbpkmp.presentation.components.wire

import androidx.compose.ui.graphics.drawscope.DrawScope

interface Wire {

    fun drawFunction(): DrawScope.() -> Unit

}