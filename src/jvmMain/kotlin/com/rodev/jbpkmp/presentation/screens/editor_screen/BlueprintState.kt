package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.runtime.mutableStateListOf
import com.rodev.jbpkmp.domain.model.Blueprint
import com.rodev.jbpkmp.domain.model.graph.EventGraph
import com.rodev.jbpkmp.domain.model.graph.FunctionGraph
import com.rodev.jbpkmp.domain.model.variable.LocalVariable

class BlueprintState(
    val eventGraph: GraphState,
    functions: List<FunctionState> = emptyList(),
    processes: List<ProcessState> = emptyList(),
    variables: List<GlobalVariableState> = emptyList()
) {

    val globalVariables = mutableStateListOf<GlobalVariableState>()
    val functionGraphs = mutableStateListOf<FunctionState>()
    val processGraphs = mutableStateListOf<ProcessState>()

    init {
        functionGraphs.addAll(functions)
        processGraphs.addAll(processes)
        globalVariables.addAll(variables)
    }

    inline fun forEachGraph(func: (GraphState) -> Unit) {
        func(eventGraph)
        functionGraphs.forEach { func(it.graphState) }
        processGraphs.forEach { func(it.graphState) }
    }

    fun findGraphById(id: String): InvokableState {


        return functionGraphs.firstOrNull { it.id == id }
            ?:
            processGraphs.first { it.id == id }
    }

    private inline fun <T> mapLocalVariables(func: (LocalVariableState) -> T): List<T> {
        return mutableListOf<T>().apply {
            forEachGraph {
                addAll(it.variables.map(func))
            }
        }
    }

    fun toBlueprint(): Blueprint {
        return Blueprint(
            localVariables = mapLocalVariables { it.toLocalVariable() },
            globalVariables = globalVariables.map { it.toGlobalVariable() },
            eventGraph = EventGraph(
                id = eventGraph.id,
                variables = eventGraph.variables.map { it.id },
                graph = eventGraph.viewModel.save()
            ),
            processes = processGraphs.map { it.toGraph() },
            functions = functionGraphs.map { it.toGraph() }
        )
    }

    private fun InvokableState.toGraph(): FunctionGraph {
        return FunctionGraph(
            id = id,
            name = name,
            variables = graphState.variables.map { it.id },
            graph = graphState.viewModel.save()
        )
    }
}