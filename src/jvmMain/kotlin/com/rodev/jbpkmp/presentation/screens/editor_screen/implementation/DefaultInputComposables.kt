package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation

import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.rodev.nodeui.components.pin.DefaultValueComposable
import com.rodev.nodeui.components.pin.PinState

class StringInputComposable : DefaultValueComposable {

    private var input by mutableStateOf("")

    @Composable
    override fun draw(pinState: PinState) {
        TextField(input, onValueChange = { input = it} )
    }

    override fun getValue(): String {
        return input
    }

    override fun setValue(any: String?) {
        input = any ?: ""
    }

}