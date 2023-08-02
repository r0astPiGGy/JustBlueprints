package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import com.rodev.jbpkmp.defaultViewPortViewModel
import com.rodev.jbpkmp.domain.model.Blueprint
import com.rodev.jbpkmp.domain.model.Project
import com.rodev.jbpkmp.domain.model.graph.EventGraph
import com.rodev.jbpkmp.domain.model.loadBlueprint
import com.rodev.jbpkmp.domain.model.saveBlueprint
import com.rodev.jbpkmp.domain.model.variable.GlobalVariable
import com.rodev.jbpkmp.domain.model.variable.LocalVariable
import com.rodev.jbpkmp.domain.model.variable.Variable
import com.rodev.nodeui.components.node.NodeState
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json

class EditorScreenViewModel(
    projectPath: String
) : SelectionHandler {

    private val json = Json { prettyPrint = true }
    private val project: Project

    var currentGraph by mutableStateOf<GraphState?>(null)
        private set

    val state = EditorScreenState(isLoading = true)

    private var loadingJob: Job? = null
    private var selectable: Selectable? = null

    init {
        project = Project.loadFromFolder(projectPath)
        // load current graph
        loadingJob = CoroutineScope(Dispatchers.Default).launch {
            load()
            state.isLoading = false
            loadingJob = null
        }
    }

    private suspend fun load() {
        val blueprint = project.loadBlueprint()
        val eventGraph = blueprint.eventGraph

        delay(500)

        val viewModel = defaultViewPortViewModel(
            selectionHandler = this
        )

        viewModel.load(eventGraph.graph)

        currentGraph = GraphState(
            viewModel = viewModel,
            variables = eventGraph.localVariables.map { it.toState() }
        )

        state.variables.addAll(
            eventGraph.globalVariables.map { it.toState() }
        )
    }

    override fun onSelect(selectable: Selectable) {
        this.selectable?.let {
            it.selected = false
        }
        this.selectable = selectable
        selectable.selected = true
    }

    fun deleteNode(nodeState: NodeState) {
        currentGraph?.viewModel?.deleteNode(nodeState)
    }

    override fun resetSelection() {
        selectable?.let {
            it.selected = false
        }
        selectable = null
    }

    @OptIn(ExperimentalComposeUiApi::class)
    fun handleKeyEvent(keyEvent: KeyEvent): Boolean {
        if (keyEvent.key == Key.Delete) {
            val selectable = this.selectable
            resetSelection()
            selectable?.onDelete(this)
            return true
        }

        return false
    }

    fun onEvent(event: EditorScreenEvent) {
        when (event) {
            is EditorScreenEvent.BuildProject -> {

            }

            is EditorScreenEvent.SaveProject -> {
                currentGraph?.let(::saveGraphModel)
            }

            is EditorScreenEvent.AddLocalVariable -> {
                currentGraph?.variables?.add(event.variable)
            }

            is EditorScreenEvent.AddGlobalVariable -> {
                state.variables.add(event.variable)
            }

            is EditorScreenEvent.OnDragAndDrop -> {
                handleDragAndDropEvent(event.variable, event.position)
            }
        }
    }

    private fun handleDragAndDropEvent(variableState: VariableState, position: Offset) {
        val currentGraph = currentGraph ?: return

        val scrollPosition = currentGraph.viewModel.scrollState.let {
            Offset(it.xValue.toFloat(), it.yValue.toFloat())
        }

        val targetPosition = scrollPosition + position

        when (variableState) {
            is GlobalVariableState -> {}
            is LocalVariableState -> {}
        }
    }

    fun onDispose() {
        resetSelection()
        onEvent(EditorScreenEvent.SaveProject)
        loadingJob?.cancel()
        loadingJob = null
    }

    private fun saveGraphModel(graphState: GraphState) {
        val graph = graphState.viewModel.save()

        project.saveBlueprint(
            json = json,
            blueprint = Blueprint(
                eventGraph = EventGraph(
                    localVariables = graphState.variables.map { it.toLocalVariable() },
                    globalVariables = state.variables.map { it.toGlobalVariable() },
                    graph = graph
                ),
                processes = emptyList(),
                functions = emptyList()
            )
        )
    }
}