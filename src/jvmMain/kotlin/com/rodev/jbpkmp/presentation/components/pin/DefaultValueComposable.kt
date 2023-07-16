package com.rodev.jbpkmp.presentation.components.pin

import androidx.compose.runtime.Composable

interface DefaultValueComposable {

    @Composable
    fun draw(pinState: PinState)

    fun getValue(): Any?

    fun setValue(any: Any?)

}

object EmptyDefaultValueComposable : DefaultValueComposable {
    @Composable
    override fun draw(pinState: PinState) {}

    override fun getValue(): Any? = null

    override fun setValue(any: Any?) {}
}

fun DefaultValueComposable.visibleIfNotConnected(): DefaultValueComposable {
    return ConnectedPinVisibilityWrapper(this)
}

private class ConnectedPinVisibilityWrapper(
    private val defaultValueComposable: DefaultValueComposable
) : DefaultValueComposable {

    @Composable
    override fun draw(pinState: PinState) {
        if (pinState.connected) return

        defaultValueComposable.draw(pinState)
    }

    override fun getValue(): Any? = defaultValueComposable.getValue()

    override fun setValue(any: Any?) {
        defaultValueComposable.setValue(any)
    }

}