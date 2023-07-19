package com.rodev.nodeui.components.pin

import androidx.compose.runtime.Composable

interface DefaultValueComposable {

    @Composable
    fun draw(pinState: PinState)

    fun getValue(): String?

    fun setValue(any: String?)

}

object EmptyDefaultValueComposable : DefaultValueComposable {
    @Composable
    override fun draw(pinState: PinState) {}

    override fun getValue(): String? = null

    override fun setValue(any: String?) {}
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

    override fun getValue(): String? = defaultValueComposable.getValue()

    override fun setValue(any: String?) {
        defaultValueComposable.setValue(any)
    }

}