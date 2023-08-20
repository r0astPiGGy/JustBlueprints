package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation

import com.rodev.jbpkmp.domain.compiler.Nodes
import com.rodev.jbpkmp.domain.model.graph.GraphEntity
import com.rodev.jbpkmp.domain.source.NodeDataSource
import com.rodev.jbpkmp.domain.source.getNodeById
import com.rodev.jbpkmp.presentation.screens.editor_screen.*
import com.rodev.jbpkmp.util.generateUniqueId
import com.rodev.nodeui.components.graph.NodeAddEvent
import com.rodev.nodeui.model.Node

class GraphStateFactoryImpl(
    private val viewModelFactory: ViewPortViewModelFactory,
    private val variableStateProvider: VariableStateProvider,
    private val nodeDataSource: NodeDataSource
) : GraphStateFactory {

    private inline fun createGraph(
        graph: GraphEntity,
        viewModelFunction: (ViewPortViewModelFactory) -> ViewPortViewModel
    ): GraphState {
        val variables = graph.variables.mapNotNull {
            variableStateProvider.getVariableStateById(it) as? LocalVariableState
        }
        val viewModel = viewModelFunction(viewModelFactory)
        viewModel.load(graph.graph)

        return GraphState(
            id = graph.id,
            viewModel = viewModel,
            variables = variables
        )
    }

    override fun createEventGraph(graph: GraphEntity): GraphState {
        return createGraph(graph, ViewPortViewModelFactory::createEventGraphViewModel)
    }

    override fun createProcessGraph(graph: GraphEntity): GraphState {
        return createGraph(graph, ViewPortViewModelFactory::createProcessGraphViewModel)
    }

    override fun createFunctionGraph(graph: GraphEntity): GraphState {
        return createGraph(graph, ViewPortViewModelFactory::createFunctionGraphViewModel)
    }

    override fun createProcessGraph(): GraphState {
        val viewModel = viewModelFactory.createProcessGraphViewModel()
        val id = generateUniqueId()

        // add process declaration to the viewModel
        val node = nodeDataSource.getNodeById(Nodes.Type.PROCESS_DECLARATION).setInvokableId(id)
        viewModel.onEvent(NodeAddEvent(node))

        return GraphState(
            id = id,
            viewModel = viewModel
        )
    }

    override fun createFunctionGraph(): GraphState {
        val viewModel = viewModelFactory.createFunctionGraphViewModel()
        val id = generateUniqueId()

        // add function declaration to the viewModel
        val node = nodeDataSource.getNodeById(Nodes.Type.FUNCTION_DECLARATION).setInvokableId(id)
        viewModel.onEvent(NodeAddEvent(node))

        return GraphState(
            id = id,
            viewModel = viewModel
        )
    }
}