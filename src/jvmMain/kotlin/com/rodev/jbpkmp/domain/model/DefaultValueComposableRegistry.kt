package com.rodev.jbpkmp.domain.model

import com.rodev.generator.action.entity.PinModel
import com.rodev.nodeui.components.pin.DefaultValueComposable
import com.rodev.nodeui.components.pin.EmptyDefaultValueComposable

interface DefaultValueComposableRegistry {

    fun create(pinModel: PinModel): DefaultValueComposable

    companion object {

        fun create(scope: Scope.() -> Unit): DefaultValueComposableRegistry {
            return DefaultValueComposableRegistryImpl().apply(scope)
        }

    }

    interface Scope {

        fun register(type: String, provider: DefaultValueComposableProvider)

    }
}


typealias DefaultValueComposableProvider = (PinModel) -> DefaultValueComposable

private class DefaultValueComposableRegistryImpl : DefaultValueComposableRegistry, DefaultValueComposableRegistry.Scope {

    private val providers = hashMapOf<String, DefaultValueComposableProvider>()

    override fun create(pinModel: PinModel): DefaultValueComposable {
        return providers[pinModel.type]?.invoke(pinModel) ?: EmptyDefaultValueComposable
    }

    override fun register(type: String, provider: DefaultValueComposableProvider) {
        providers[type] = provider
    }

}