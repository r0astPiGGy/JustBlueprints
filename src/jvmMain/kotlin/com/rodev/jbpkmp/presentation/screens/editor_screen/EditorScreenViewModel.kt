package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import com.rodev.jbpkmp.data.GlobalDataSource
import com.rodev.jbpkmp.domain.model.Blueprint
import com.rodev.jbpkmp.domain.model.Project
import com.rodev.jbpkmp.domain.model.graph.EventGraph
import com.rodev.jbpkmp.domain.model.loadBlueprint
import com.rodev.jbpkmp.domain.model.saveBlueprint
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.CreateVariableGraphEvent
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.DefaultPinTypeComparator
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.ViewPortViewModel
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node.DefaultNodeStateFactory
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node.NodeStateFactoryRegistry
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node.VariableNodeDisplay
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node.VariableNodeStateFactory
import com.rodev.nodeui.components.node.NodeState
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json

class EditorScreenViewModel(
    projectPath: String
) : SelectionHandler, VariableStateProvider {

    private val json = Json { prettyPrint = true }
    private val project: Project

    var currentGraph by mutableStateOf<GraphState?>(null)
        private set

    val state = EditorScreenState(isLoading = true)

    private var loadingJob: Job? = null

    var selectable: Selectable? by mutableStateOf(null)
        private set

    private val variablesById = hashMapOf<String, VariableState>()

    private val selectionActionVisitor: SelectionActionVisitor = SelectionActionVisitorImpl()
    private val nodeStateFactory = createNodeStateFactory()

    init {
        project = Project.loadFromFolder(projectPath)
        // load current graph
        loadingJob = CoroutineScope(Dispatchers.Default).launch {
            load()
            state.isLoading = false
            loadingJob = null
        }
    }

    private fun createNodeStateFactory() = NodeStateFactoryRegistry().apply {
        setDefaultNodeStateFactory(
            DefaultNodeStateFactory(
                nodeDataSource = GlobalDataSource,
                nodeTypeDataSource = GlobalDataSource,
                actionDataSource = GlobalDataSource,
                pinTypeDataSource = GlobalDataSource,
                selectorDataSource = GlobalDataSource,
                selectionHandler = this@EditorScreenViewModel,
                actionDetailsDataSource = GlobalDataSource
            )
        )
        registerNodeStateFactory(
            typeId = VARIABLE_TYPE_TAG, VariableNodeStateFactory(
                selectionHandler = this@EditorScreenViewModel,
                variableStateProvider = this@EditorScreenViewModel,
                pinTypeDataSource = GlobalDataSource
            )
        )
    }

    private fun createViewPortViewModel(): ViewPortViewModel {
        return ViewPortViewModel(
            pinTypeComparator = DefaultPinTypeComparator,
            nodeStateFactory = nodeStateFactory,
            actionDataSource = GlobalDataSource,
            nodeDataSource = GlobalDataSource
        )
    }

    private suspend fun load() {
        val blueprint = project.loadBlueprint()
        val eventGraph = blueprint.eventGraph

        delay(500)

        val viewModel = createViewPortViewModel()

        val localVariables = eventGraph.localVariables.map { it.toState() }
        val globalVariables = eventGraph.globalVariables.map { it.toState() }

        localVariables.forEach {
            variablesById[it.id] = it
        }

        globalVariables.forEach {
            variablesById[it.id] = it
        }

        viewModel.load(eventGraph.graph)

        currentGraph = GraphState(
            viewModel = viewModel,
            variables = localVariables
        )

        state.variables.addAll(
            globalVariables
        )
    }

    override fun onSelect(selectable: Selectable) {
        this.selectable?.let {
            it.selected = false
        }
        this.selectable = selectable
        selectable.selected = true
    }

    private fun deleteNode(nodeState: NodeState) {
        currentGraph?.viewModel?.deleteNode(nodeState)
    }

    private fun deleteLocalVariable(variable: LocalVariableState) {
        currentGraph?.let { graph ->
            graph.removeVariable(variable)
            graph.variables.removeIf { it.id == variable.id }
            variablesById.remove(variable.id)
        }
    }

    private fun deleteGlobalVariable(variable: GlobalVariableState) {
        currentGraph?.let { graph ->
            graph.removeVariable(variable)
            state.variables.removeIf { it.id == variable.id }
            variablesById.remove(variable.id)
        }
    }

    private fun GraphState.removeVariable(variable: VariableState) {
        viewModel.nodeStates.filter { node ->
            val representation = node.nodeDisplay
            if (representation is VariableNodeDisplay) {
                return@filter representation.variableId == variable.id
            }

            false
        }.let { nodes ->
            viewModel.deleteNodes(nodes)
        }
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
            handleDeleteEvent()
            return true
        }

        return false
    }

    private fun handleDeleteEvent() {
        val selectable = this.selectable
        resetSelection()

        selectable?.onDelete(selectionActionVisitor)
    }

    fun onEvent(event: EditorScreenEvent) {
        when (event) {
            is EditorScreenEvent.BuildProject -> {

            }

            is EditorScreenEvent.SaveProject -> {
                currentGraph?.let(::saveGraphModel)
            }

            is EditorScreenEvent.AddLocalVariable -> {
                currentGraph?.let {
                    val variable = event.variable

                    variablesById[variable.id] = variable
                    it.variables.add(variable)
                }
            }

            is EditorScreenEvent.AddGlobalVariable -> {
                event.variable.let {
                    variablesById[it.id] = it
                    state.variables.add(it)
                }
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

        currentGraph.viewModel.onEvent(
            CreateVariableGraphEvent(variableState, targetPosition)
        )
    }

    override fun getVariableStateById(id: String): VariableState? {
        return variablesById[id]
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

    private inner class SelectionActionVisitorImpl : SelectionActionVisitor {
        override fun deleteNode(nodeState: NodeState) {
            this@EditorScreenViewModel.deleteNode(nodeState)
        }

        override fun deleteLocalVariable(variable: LocalVariableState) {
            this@EditorScreenViewModel.deleteLocalVariable(variable)
        }

        override fun deleteGlobalVariable(variable: GlobalVariableState) {
            this@EditorScreenViewModel.deleteGlobalVariable(variable)
        }

    }
}