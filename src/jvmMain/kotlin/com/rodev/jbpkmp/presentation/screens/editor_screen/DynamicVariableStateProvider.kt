package com.rodev.jbpkmp.presentation.screens.editor_screen

class DynamicVariableStateProvider : VariableStateProvider {

    private var variableStateProvider: VariableStateProvider? = null

    fun setVariableStateProvider(variableStateProvider: VariableStateProvider) {
        this.variableStateProvider = variableStateProvider
    }

    fun unregister() {
        variableStateProvider = null
    }

    override fun getVariableStateById(id: String): VariableState? {
        val provider = variableStateProvider

        require(provider != null) { "VariableStateProvider is not set." }

        return provider.getVariableStateById(id)
    }

}