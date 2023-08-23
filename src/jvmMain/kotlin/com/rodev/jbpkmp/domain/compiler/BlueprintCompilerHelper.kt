package com.rodev.jbpkmp.domain.compiler

import com.rodev.generator.action.entity.NodeModel
import com.rodev.generator.action.entity.PinModel
import com.rodev.generator.action.entity.Pins
import com.rodev.generator.action.entity.extra_data.*
import com.rodev.generator.action.utils.toMap
import com.rodev.jbp.compiler.Actions
import com.rodev.jbp.compiler.module.CodeContainer
import com.rodev.jbp.compiler.module.Handlers
import com.rodev.jbp.compiler.module.action.CodeAction
import com.rodev.jbp.compiler.module.action.CodeBasicAction
import com.rodev.jbp.compiler.module.action.CodeContainingAction
import com.rodev.jbp.compiler.module.handler.CodeEvent
import com.rodev.jbp.compiler.module.handler.CodeFunction
import com.rodev.jbp.compiler.module.handler.CodeHandler
import com.rodev.jbp.compiler.module.handler.CodeProcess
import com.rodev.jbp.compiler.module.value.EmptyValue
import com.rodev.jbp.compiler.module.value.GameValue
import com.rodev.jbp.compiler.module.value.Value
import com.rodev.jbp.compiler.module.value.ValueType
import com.rodev.jbp.compiler.module.value.constants.*
import com.rodev.jbpkmp.domain.compiler.Nodes.Factory.Companion.toFactory
import com.rodev.jbpkmp.domain.compiler.exception.BlueprintCompileException
import com.rodev.jbpkmp.domain.model.Blueprint
import com.rodev.jbpkmp.domain.model.graph.FunctionGraph
import com.rodev.jbpkmp.domain.model.variable.GlobalVariable
import com.rodev.jbpkmp.domain.model.variable.LocalVariable
import com.rodev.jbpkmp.domain.model.variable.Variable
import com.rodev.jbpkmp.domain.source.NodeDataSource
import com.rodev.jbpkmp.presentation.screens.editor_screen.*
import com.rodev.jbpkmp.util.castOrNull
import com.rodev.jbpkmp.util.contains
import com.rodev.nodeui.model.Graph
import com.rodev.nodeui.model.Node
import com.rodev.nodeui.model.Pin
import com.rodev.nodeui.model.PinConnection

class BlueprintCompilerHelper(
    private val nodeDataSource: NodeDataSource,
    blueprint: Blueprint
) {

    private val connections: List<PinConnection>
    private val graphNodes: List<Node>

    init {

        // merge all graphs
        val (connections, graphNodes) = listOf(blueprint.eventGraph.graph).asSequence()
            .plus(blueprint.processes.map(FunctionGraph::graph))
            .plus(blueprint.functions.map(FunctionGraph::graph))
            .fold(mutableListOf<PinConnection>() to mutableListOf<Node>()) { acc, graph ->
                acc.first.addAll(graph.connections)
                acc.second.addAll(graph.nodes)
                acc
            }

        this.connections = connections
        this.graphNodes = graphNodes
    }

    private val localVariables = blueprint.localVariables
    private val globalVariables = blueprint.globalVariables
    private val processes = blueprint.processes
    private val functions = blueprint.functions
    private val localVariablesById = localVariables.toMap(LocalVariable::id)
    private val globalVariablesById = globalVariables.toMap(GlobalVariable::id)
    private val valueFactory = ValueFactory

    private val nodes = mutableListOf<NodeAdapter>()
    private val pinsById = mutableMapOf<String, PinAdapter>()
    private val invokableHandlerNamesById = mutableMapOf<String, String>()

    fun compile(): String {
        interpretNodes()
        connectPins()
        findInvokableHandlers()

        val handlers = Handlers().apply {
            this += adaptHandlers()
        }

        return handlers.toJson().toString()
    }

    private fun findInvokableHandlers() {
        listOf(functions, processes).forEach { list ->
            list.forEach {
                invokableHandlerNamesById[it.id] = it.name
            }
        }
    }

    private fun adaptHandlers() = nodes.mapNotNull {
        listOf(
            adaptProcess(it),
            adaptEvent(it),
            adaptFunction(it)
        ).firstOrNull { handler -> handler != null }
    }

    private fun <T : CodeHandler> adaptHandler(
        node: NodeAdapter,
        handlerFactory: (NodeModelAdapter) -> T?
    ): T? {
        if (node !is NodeModelAdapter) return null

        val handler = handlerFactory(node) ?: return null

        CodeContainerScope.using(handler) {
            val outputExecPin = node.findOutputExecPin()

            require(outputExecPin != null) {
                "Output exec pin not found in node ${node.nodeModel}"
            }

            outputExecPin.findNextNodes().forEach {
                it.onAdapt(this)
            }
        }

        return handler
    }

    private fun adaptProcess(node: NodeAdapter): CodeProcess? {
        return adaptHandler(node) {
            if (it.nodeModel.id != Nodes.Type.PROCESS_DECLARATION)
                return@adaptHandler null

            val processId = node.node.getInvokableId()
            val processName = invokableHandlerNamesById[processId]!!

            CodeProcess(name = processName)
        }
    }

    private fun adaptFunction(node: NodeAdapter): CodeFunction? {
        return adaptHandler(node) {
            if (it.nodeModel.id != Nodes.Type.FUNCTION_DECLARATION)
                return@adaptHandler null

            val functionId = node.node.getInvokableId()
            val functionName = invokableHandlerNamesById[functionId]!!

            CodeFunction(name = functionName)
        }
    }

    private fun adaptEvent(node: NodeAdapter): CodeEvent? {
        return adaptHandler(node) {
            val model = it.nodeModel
            val handlerExtraData = model.extra.castOrNull<HandlerExtraData>() ?: return@adaptHandler null

            CodeEvent(handlerExtraData.id)
        }
    }

    private fun CodeContainerScope.adaptBranch(adapter: NodeModelAdapter) {
        val conditionPin = adapter.inputPins.findPin { it.pinModel.type == Pins.Type.CONDITION }!!
        val connectedConditionNode = conditionPin.connectedPins.firstOrNull()?.owner ?: return

        val conditionAction = connectedConditionNode.adaptSelf().let {
            CodeContainingAction(
                id = it.id,
                args = it.args,
                selection = it.selection,
                conditional = it.conditional
            )
        }

        val invertConditionPin = adapter.inputPins.findPin {
            it.pinModel.extra.contains<InvertConditionExtraData>()
        }
        val inverted = invertConditionPin?.pin?.getValue().toString().lowercase() == "true"

        conditionAction.isInverted = inverted

        CodeContainerScope.using(conditionAction) {
            adapter.outputPins[0].findNextNodes().forEach {
                it.onAdapt(this)
            }
        }

        push(conditionAction)

        val elseAction = CodeContainingAction(id = Actions.ELSE, conditional = null)
        val elsePin = adapter.outputPins[1]

        // Не добавляем else, если пусто
        if (elsePin.connections.isEmpty()) return

        CodeContainerScope.using(elseAction) {
            elsePin.findNextNodes().forEach {
                it.onAdapt(this)
            }
        }

        push(elseAction)

    }

    private fun CodeContainerScope.adaptFunctionReference(adapter: NodeModelAdapter) {
        val invokableId = adapter.node.getInvokableId()
        val functionName = invokableHandlerNamesById[invokableId] ?: return

        push(Nodes.Action.CALL_FUNCTION(functionName))
    }

    private fun CodeContainerScope.adaptProcessReference(adapter: NodeModelAdapter) {
        val invokableId = adapter.node.getInvokableId()
        val processName = invokableHandlerNamesById[invokableId] ?: return
        val args = ArgumentAdapter(adapter.inputPins).also(ArgumentAdapter::adapt).args

        push(Nodes.Action.START_PROCESS(processName, args))
    }

    private fun CodeContainerScope.handleCustomModel(id: String, adapter: NodeModelAdapter): Boolean {
        when (id) {
            Nodes.Type.BRANCH -> adaptBranch(adapter)
            Nodes.Type.PROCESS_REFERENCE -> adaptProcessReference(adapter)
            Nodes.Type.FUNCTION_REFERENCE -> adaptFunctionReference(adapter)
            else -> return false
        }

        return true
    }

    private inner class ArgumentAdapter(
        private val inputPins: List<PinAdapter>
    ) {

        var selection: String? = null
        var conditional: CodeAction? = null
        var inverted = false
        val args = mutableMapOf<String, Value>()

        fun adapt() {
            inputPins.mapNotNull { it as? PinModelAdapter }.forEach {
                when {
                    it.pinModel.type == Pins.Type.CONDITION -> {
                        conditional = it.connectedPins.firstOrNull()?.owner?.adaptSelf()
                    }
                    Pins.Type.SELECTOR.contains(it.pinModel.type) -> {
                        selection = it.pin.getValue()
                    }
                    it.pinModel.extra.contains<InvertConditionExtraData>() -> {
                        inverted = it.pin.getValue().toString().lowercase() == "true"
                    }
                    it.pinModel.type == Pins.Type.EXECUTION -> {
                        // ignore
                    }
                    else -> {
                        args[it.pinModel.id] = it.asArgument()
                    }
                }
            }
        }

    }

    private fun CodeContainerScope.adaptModel(adapter: NodeModelAdapter) {
        val nodeModel = adapter.nodeModel
        val extraData = nodeModel.extra

        if (handleCustomModel(nodeModel.id, adapter)) return

        val argumentAdapter = ArgumentAdapter(adapter.inputPins).also(ArgumentAdapter::adapt)

        val id = nodeModel.id
        val selection: String? = argumentAdapter.selection
        val conditional: CodeAction? = argumentAdapter.conditional
        val inverted = argumentAdapter.inverted
        val args = argumentAdapter.args

        val codeAction = if (extraData.contains<ContainerExtraData>()) {
            CodeContainingAction(
                id = id,
                args = args,
                selection,
                conditional
            ).apply {
                adaptAll(adapter)
                isInverted = inverted
            }
        } else if (inverted) {
            CodeContainingAction(
                id = id,
                args = args,
                selection,
                conditional
            ).apply {
                isInverted = true
            }
        } else {
            CodeBasicAction(
                id = id,
                args = args,
                selection,
                conditional
            )
        }

        push(codeAction)
    }

    private fun PinModelAdapter.asArgument(type: ValueType): Value {
        val connection = connectedPins.firstOrNull()
        val connected = connection != null
        val variableConstant = (connection as? VariablePinAdapter)?.owner?.variable?.toVariableConstant()

        return when (type) {
            ValueType.Array -> {
                if (!connected) {
                    return ArrayConstant(emptyList())
                }

                if (variableConstant != null) {
                    return variableConstant
                }

                val output = (connection as PinModelAdapter).asOutput()

                if (output.type != type) {
                    return ArrayConstant(listOf(output))
                }

                output
            }
            ValueType.Map -> {
                if (!connected) {
                    return MapConstant(emptyMap())
                }

                if (variableConstant != null) {
                    return variableConstant
                }

                val output = (connection as PinModelAdapter).asOutput()

                if (output.type == ValueType.GameValue) {
                    return output
                }

                requireEqualTypes(output.type, type)

                output
            }
            ValueType.Number -> {
                if (!connected) {
                    return NumberConstant(pin.getValue()?.toDoubleOrNull() ?: 0)
                }

                if (variableConstant != null) {
                    return variableConstant
                }

                val output = (connection as PinModelAdapter).asOutput()

                if (output.type == ValueType.GameValue) {
                    return output
                }

                if (output.type == ValueType.Array) {
                    return output
                }

                requireEqualTypes(output.type, type)

                output
            }
            ValueType.Text -> {
                if (!connected) {
                    return TextConstant(pin.getValue().toString())
                }

                if (variableConstant != null) {
                    return variableConstant
                }

                val output = (connection as PinModelAdapter).asOutput()

                if (output.type == ValueType.GameValue) {
                    return output
                }

                if (output.type == ValueType.Array) {
                    return output
                }

                requireEqualTypes(output.type, type)

                output
            }
            ValueType.Variable -> {
                variableConstant?.let { return it }

                return EmptyValue
            }
            ValueType.Any -> {
                if (!connected) return EmptyValue

                if (variableConstant != null) return variableConstant

                (connection as PinModelAdapter).asOutput()
            }
            ValueType.GameValue -> throw IllegalStateException()
            ValueType.Enum -> throw IllegalStateException()
            ValueType.Empty -> throw IllegalStateException()
            else -> { // Item, Location, Particle, Sound, Vector
                if (!connected) return EmptyValue

                if (variableConstant != null) {
                    return variableConstant
                }

                val output = (connection as PinModelAdapter).asOutput()

                if (output.type == ValueType.GameValue) {
                    return output
                }

                if (output.type == ValueType.Array) {
                    return output
                }

                requireEqualTypes(output.type, type)

                output
            }
        }
    }

    private fun PinModelAdapter.asArgument(): Value {
        val model = pinModel
        val value = pin.getValue()

        model.extra.castOrNull<EnumExtraData>()?.let {
            // Если это енум, то value не может быть нулл
            return EnumConstant(value!!)
        }

        if (model.type == Pins.Type.BOOLEAN) {
            return BooleanConstant(value.toString().lowercase() == "true")
        }

        val type = getValueType() ?: throw IllegalStateException("Unknown type: ${model.type}")

        return asArgument(type)
    }

    @Throws(BlueprintCompileException::class)
    private fun PinModelAdapter.requireEqualTypes(actual: ValueType?, expected: ValueType) {
        if (expected != actual) {
            throw BlueprintCompileException.WrongArgument(
                expected = expected,
                actual = actual,
                nodeId = owner.node.uniqueId,
                pinId = pin.uniqueId
            )
        }
    }

    private fun PinModelAdapter.asOutput(): Value {
        owner as NodeModelAdapter

        val nodeModel = owner.nodeModel

        nodeModel.extra.castOrNull<GameValueExtraData>()?.let { extra ->
            val selectorPin = owner.inputPins.findPin { Pins.Type.SELECTOR.contains(it.pinModel.type) }

            return GameValue(
                value = extra.id,
                selection = selectorPin?.pin?.getValue()
            )
        }

        val type = getValueType()

        if (type == ValueType.Array) {
            val mutableList = mutableListOf<Value>()

            owner.inputPins.map {
                (it as PinModelAdapter).asArgument(ValueType.Any)
            }.forEach {
                if (it is ArrayConstant) {
                    // Throw exception ?
                    mutableList.addAll(it.values)
                } else {
                    mutableList.add(it)
                }
            }
            return ArrayConstant(mutableList)
        }

        val factory = type?.toFactory()

        // TODO Find factory by pin owner type id
        require(factory != null) { "Type not supported: $type" }

        return valueFactory.createValue(factory, owner.node)
    }

    private fun PinModelAdapter.getValueType(): ValueType? {
        return ValueType.values().find { it.id == pinModel.type }
    }

    private fun CodeContainingAction.adaptAll(nodeAdapter: NodeModelAdapter) {
        CodeContainerScope.using(this) {
            nodeAdapter.findContainerExecPin()!!.findNextNodes().forEach {
                it.onAdapt(this)
            }
        }
    }

    private fun NodeAdapter.adaptSelf(): CodeAction {
        return with(SingleCodeActionScope()) {
            onAdapt(this)
            codeAction!!
        }
    }

    private class SingleCodeActionScope : CodeContainerScope {

        var codeAction: CodeAction? = null
            private set

        override fun push(codeAction: CodeAction) {
            require(this.codeAction == null) { "Not supported" }

            this.codeAction = codeAction
        }

    }

    private fun PinAdapter.findNextNodes(): List<NodeAdapter> {
        val list = mutableListOf<NodeAdapter>()

        var nextNode = findNext()
        while (nextNode != null) {
            list.add(nextNode)
            nextNode = nextNode.findOutputExecPin()?.findNext()
        }

        return list
    }

    private fun PinAdapter.findNext(): NodeAdapter? {
        return connectedPins.firstOrNull()?.owner
    }

    private fun NodeAdapter.findContainerExecPin(): PinAdapter? {
        return findOutputExecPin { it.contains<ExecContainerExtraData>() }
    }

    private fun NodeAdapter.findOutputExecPin(): PinAdapter? {
        return findOutputExecPin { it.contains<ExecNextExtraData>() }
    }

    private fun NodeAdapter.findOutputExecPin(predicate: (ExtraData?) -> Boolean): PinAdapter? {
        if (this !is NodeModelAdapter) return null

        return outputPins.findPin {
            predicate(it.pinModel.extra)
        }
    }

    private fun List<PinAdapter>.findPin(predicate: (PinModelAdapter) -> Boolean): PinAdapter? {
        return mapNotNull { it as? PinModelAdapter }.find(predicate)
    }

    private fun connectPins() {
        connections.forEach {
            val inputPin = pinsById[it.inputPinId]!!
            val outputPin = pinsById[it.outputPinId]!!

            inputPin.connections.add(it.outputPinId)
            outputPin.connections.add(it.inputPinId)
        }
    }

    private fun interpretNodes() {
        graphNodes.forEach { node ->
            val typeId = node.getType()
            if (typeId == VARIABLE_TYPE_TAG) {
                interpretVariableNode(node).apply {
                    interpretPins()
                }
            } else {
                interpretDefaultNode(typeId, node).apply {
                    interpretPins()
                }
            }
        }
    }

    private fun VariableNodeAdapter.interpretPins() {
        val pin = node.outputPins[0]

        val pinAdapter = VariablePinAdapter(pin, this)

        outputPins.add(pinAdapter)
        pinsById[pin.uniqueId] = pinAdapter
    }

    private fun NodeModelAdapter.interpretPins() {
        val model = nodeModel

        fun findPinByTypeId(typeId: String): PinModel? {
            return model.input.find { it.id == typeId } ?: model.output.find { it.id == typeId }
        }

        fun interpret(pins: List<Pin>): List<PinAdapter> {
            return pins.map {
                val typeId = it.getId()

                PinModelAdapter(
                    model = findPinByTypeId(typeId),
                    pin = it,
                    owner = this
                )
            }.onEach {
                pinsById[it.pin.uniqueId] = it
            }
        }

        interpret(node.inputPins).forEach {
            inputPins.add(it)
        }

        interpret(node.outputPins).forEach {
            outputPins.add(it)
        }
    }

    private fun interpretVariableNode(node: Node): VariableNodeAdapter {
        val variableId = node.getString(VARIABLE_ID_TAG)

        val variable = localVariablesById[variableId] ?: globalVariablesById[variableId]

        require(variable != null) { "Variable by id $variableId not found." }

        return VariableNodeAdapter(
            node,
            variable
        ).apply {
            nodes.add(this)
        }
    }

    private fun interpretDefaultNode(typeId: String, node: Node): NodeModelAdapter {
        val nodeModel = nodeDataSource.getNodeModelById(typeId)!!

        return NodeModelAdapter(nodeModel, node).apply {
            nodes.add(this)
        }
    }

    interface CodeContainerScope {

        fun push(codeAction: CodeAction)

        companion object {

            fun using(codeContainer: CodeContainer, scope: CodeContainerScope.() -> Unit) {
                with(CodeContainerScopeInstance()) {
                    scope()
                    applyTo(codeContainer)
                }
            }

        }
    }

    private class CodeContainerScopeInstance : CodeContainerScope {

        private val actions = mutableListOf<CodeAction>()

        override fun push(codeAction: CodeAction) {
            actions += codeAction
        }

        fun applyTo(codeContainer: CodeContainer) {
            codeContainer.actions.addAll(actions)
        }
    }

    abstract class NodeAdapter(
        val node: Node
    ) {

        val inputPins = mutableListOf<PinAdapter>()
        val outputPins = mutableListOf<PinAdapter>()

        private var adapted: Boolean = false

        fun onAdapt(codeContainerScope: CodeContainerScope) {
            if (adapted) return

            adapted = true

            codeContainerScope.adaptNode()
        }

        protected abstract fun CodeContainerScope.adaptNode()

    }

    inner class NodeModelAdapter(
        val nodeModel: NodeModel,
        node: Node
    ) : NodeAdapter(node) {
        override fun CodeContainerScope.adaptNode() {
            adaptModel(this@NodeModelAdapter)
        }
    }

    class VariableNodeAdapter(
        node: Node,
        val variable: Variable
    ) : NodeAdapter(node) {

        override fun CodeContainerScope.adaptNode() {
            throw IllegalStateException("Not used")
        }
    }

    class VariablePinAdapter(
        pin: Pin,
        override val owner: VariableNodeAdapter
    ) : PinAdapter(pin, owner)

    abstract class PinAdapter(
        val pin: Pin,
        open val owner: NodeAdapter,
    ) {

        val connections = mutableListOf<String>()

    }

    private val PinAdapter.connectedPins: List<PinAdapter>
        get() = connections.mapNotNull(pinsById::get)

    class PinModelAdapter(
        pin: Pin,
        private val model: PinModel?,
        owner: NodeAdapter
    ) : PinAdapter(pin, owner) {

        // Critical
        val pinModel: PinModel
            get() = model!!

    }
}