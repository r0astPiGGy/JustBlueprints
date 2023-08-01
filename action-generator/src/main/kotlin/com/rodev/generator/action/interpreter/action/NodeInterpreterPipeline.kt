package com.rodev.generator.action.interpreter.action

import com.rodev.generator.action.entity.NodeCompound
import com.rodev.generator.action.entity.PinModel
import com.rodev.jmcc_extractor.entity.ActionData
import com.rodev.jmcc_extractor.entity.Argument
import com.rodev.jmcc_extractor.entity.RawActionData

class NodeInterpreterPipeline private constructor(
    private val pinInterpreterRegistry: PinInterpreterRegistry,
    private val defaultNodeInterpreter: NodeInterpreter
): NodeInterpreterScope, NodePipelineBuilderScope, NodeInterpreter {

    private var mutableAction: ActionData? = null
    private var mutableRawAction: RawActionData? = null

    private lateinit var pipeline: NodePipeline

    override val action: ActionData
        get() = mutableAction!!

    override val rawAction: RawActionData
        get() = mutableRawAction!!

    override fun interpretPin(argument: Argument): PinModel {
        return pinInterpreterRegistry.interpretPin(action, rawAction, argument)
    }

    override fun interpret(action: ActionData, rawActionData: RawActionData): NodeCompound {
        mutableAction = action
        mutableRawAction = rawActionData

        val nodeCompound = defaultNodeInterpreter.interpret(action, rawActionData)

        return pipeline.interpret(nodeCompound)
    }

    override fun pipeline(nodeCompoundInterpreter: NodeCompoundInterpreter): NodePipeline {
        return MutableNodePipeline(this, nodeCompoundInterpreter)
    }

    companion object {

        fun build(
            pinInterpreterRegistry: PinInterpreterRegistry,
            defaultNodeInterpreter: NodeInterpreter,
            scope: NodePipelineBuilderScope.() -> NodePipeline
        ): NodeInterpreterPipeline {
            return NodeInterpreterPipeline(
                pinInterpreterRegistry,
                defaultNodeInterpreter
            ).apply {
                pipeline = scope()
            }
        }
    }

}

interface NodeInterpreter {

    fun interpret(action: ActionData, rawActionData: RawActionData): NodeCompound

}

interface NodePipelineBuilderScope {

    fun pipeline(nodeCompoundInterpreter: NodeCompoundInterpreter): NodePipeline

}

private class MutableNodePipeline(
    private val parent: NodeInterpreterScope,
    private val currentInterpreter: NodeCompoundInterpreter
) : NodeInterpreterScope, NodePipeline {

    override val action: ActionData
        get() = parent.action

    override val rawAction: RawActionData
        get() = parent.rawAction

    override fun interpretPin(argument: Argument): PinModel {
        return parent.interpretPin(argument)
    }

    override fun interpret(nodeCompound: NodeCompound): NodeCompound {
        return currentInterpreter(nodeCompound)
    }

    override fun add(nextInterpreter: NodeCompoundInterpreter): NodePipeline {
        return MutableNodePipeline(this) {
            nextInterpreter(currentInterpreter(it))
        }
    }

}

interface NodePipeline {

    fun interpret(nodeCompound: NodeCompound): NodeCompound

    fun add(nextInterpreter: NodeCompoundInterpreter): NodePipeline

}

interface NodeInterpreterScope {

    val action: ActionData
    val rawAction: RawActionData

    fun interpretPin(argument: Argument): PinModel
}

typealias NodeCompoundInterpreter = NodeInterpreterScope.(NodeCompound) -> NodeCompound
