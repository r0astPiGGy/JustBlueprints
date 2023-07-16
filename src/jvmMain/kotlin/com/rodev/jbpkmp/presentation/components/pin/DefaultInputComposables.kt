package com.rodev.jbpkmp.presentation.components.pin

import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class StringInputComposable : DefaultValueComposable {

    private var input by mutableStateOf("")

    @Composable
    override fun draw(pinState: PinState) {
        TextField(input, onValueChange = { input = it} )
    }

    override fun getValue(): String {
        return input
    }

    override fun setValue(any: Any?) {
        input = any as String
    }

}