package com.rodev.generator.action.interpreter

import com.rodev.generator.action.entity.NodeCompound

class BulkNodeCompoundInterpreter(
    private val interpreters: List<ActionInterpreterWrapper>
) : ActionInterpreterWrapper {

    override fun interpret(): List<NodeCompound> {
        return mutableListOf<NodeCompound>().apply {
            interpreters.forEach { addAll(it.interpret()) }
        }
    }

    companion object {

        fun build(scope: BulkNodeCompoundInterpreterBuilderScope.() -> Unit): ActionInterpreterWrapper {
            val builderScope = BulkNodeCompoundInterpreterBuilderScopeImpl()

            scope(builderScope)

            return builderScope.build()
        }

    }

}

private class BulkNodeCompoundInterpreterBuilderScopeImpl : BulkNodeCompoundInterpreterBuilderScope {

    private val list = mutableListOf<ActionInterpreterWrapper>()

    override fun addInterpreter(nodeCompoundProvider: NodeCompoundProvider) {
        list.add(ActionInterpreterWrapperImpl(nodeCompoundProvider))
    }

    fun build(): BulkNodeCompoundInterpreter {
        return BulkNodeCompoundInterpreter(list)
    }
}

interface BulkNodeCompoundInterpreterBuilderScope {

    fun addInterpreter(nodeCompoundProvider: NodeCompoundProvider)

}

typealias NodeCompoundProvider = () -> List<NodeCompound>

private class ActionInterpreterWrapperImpl(
    private val nodeCompoundProvider: NodeCompoundProvider
) : ActionInterpreterWrapper {

    override fun interpret(): List<NodeCompound> = nodeCompoundProvider()

}

interface ActionInterpreterWrapper {

    fun interpret(): List<NodeCompound>

}