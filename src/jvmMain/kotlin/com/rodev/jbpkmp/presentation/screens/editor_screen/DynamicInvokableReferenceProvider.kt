package com.rodev.jbpkmp.presentation.screens.editor_screen

class DynamicInvokableReferenceProvider : InvokableReferenceProvider {

    private var invokableReferenceProvider: InvokableReferenceProvider? = null

    override fun getInvokableReferenceById(id: String): InvokableReference? {
        val referenceProvider = invokableReferenceProvider

        require(referenceProvider != null) {
            "InvokableReferenceProvider is not registered."
        }

        return referenceProvider.getInvokableReferenceById(id)
    }

    fun setInvokableReferenceProvider(invokableReferenceProvider: InvokableReferenceProvider) {
        this.invokableReferenceProvider = invokableReferenceProvider
    }

    fun unregister() {
        invokableReferenceProvider = null
    }

}