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
import com.rodev.jbpkmp.data.CodeUploadServiceImpl
import com.rodev.jbpkmp.domain.compiler.BlueprintCompiler
import com.rodev.jbpkmp.domain.model.*
import com.rodev.jbpkmp.domain.model.graph.EventGraph
import com.rodev.jbpkmp.domain.remote.ApiResult
import com.rodev.jbpkmp.domain.remote.CodeUploadService
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.CreateVariableGraphEvent
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.DefaultPinTypeComparator
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.ViewPortViewModel
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node.*
import com.rodev.nodeui.components.node.NodeState
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json

class EditorScreenViewModel(
    projectPath: String
) : SelectionHandler, VariableStateProvider {

    private val json = Json { prettyPrint = true }
    private val codeUploadService: CodeUploadService = CodeUploadServiceImpl()
    val project: Project

    var currentGraph by mutableStateOf<GraphState?>(null)
        private set

    val state = EditorScreenState()

    private var buildJob: Job? = null

    private val blueprintCompiler = BlueprintCompiler()

    var selectable: Selectable? by mutableStateOf(null)
        private set

    private val variablesById = hashMapOf<String, VariableState>()

    private val selectionActionVisitor: SelectionActionVisitor = SelectionActionVisitorImpl()
    private val nodeStateFactory = createNodeStateFactory()

    init {
        project = Project.loadFromFolder(projectPath)
        // load current graph
        load()
        state.result = null
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
            typeId = "native_array_factory", ArrayNodeStateFactory(
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

    private fun load() {
        val blueprint = project.loadBlueprint()
        val eventGraph = blueprint.eventGraph

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
                buildProject()
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
        val targetPosition = currentGraph.viewModel.scrollOffset + position

        currentGraph.viewModel.onEvent(
            CreateVariableGraphEvent(variableState, targetPosition)
        )
    }

    override fun getVariableStateById(id: String): VariableState? {
        return variablesById[id]
    }

    fun resetState() {
        state.reset()
    }

    fun onDispose() {
        resetSelection()
        onEvent(EditorScreenEvent.SaveProject)
        buildJob?.cancel()
        buildJob = null
    }

    private fun getBlueprint(): Blueprint? {
        return currentGraph?.let(::getBlueprint)
    }

    private fun buildProject() {
        val blueprint = getBlueprint() ?: return

        // Build project async
        buildJob = CoroutineScope(Dispatchers.Default).launch {
            build(blueprint)

            buildJob = null
        }
    }

    private suspend fun build(blueprint: Blueprint) {
        state.result = EditorScreenResult.Loading(state = LoadingState.COMPILE)

        val data = try {
            // TODO handle BlueprintCompileException
            blueprintCompiler.compile(blueprint)
        } catch (e: Exception) {
            state.result = EditorScreenResult.Error(
                stage = LoadingState.COMPILE,
                message = e.message,
                stackTrace = e.stackTraceToString()
            )
            null
        }

        data ?: return

        state.result = EditorScreenResult.Loading(state = LoadingState.UPLOAD)

        project.writeCompileOutput(data)

        when (val apiResult = codeUploadService.upload(data)) {
            is ApiResult.Failure -> {
                state.result = EditorScreenResult.Error(
                    stage = LoadingState.UPLOAD,
                    message = apiResult.message,
                    stackTrace = null
                )
            }
            is ApiResult.Exception -> {
                state.result = EditorScreenResult.Error(
                    stage = LoadingState.UPLOAD,
                    message = apiResult.exception.message,
                    stackTrace = apiResult.exception.stackTraceToString()
                )
            }
            is ApiResult.Success -> {
                state.result = EditorScreenResult.SuccessUpload(
                    uploadCommand = apiResult.data.commandToLoad
                )
            }
        }
    }

    private fun getBlueprint(graphState: GraphState): Blueprint {
        val graph = graphState.viewModel.save()

        return Blueprint(
            eventGraph = EventGraph(
                localVariables = graphState.variables.map { it.toLocalVariable() },
                globalVariables = state.variables.map { it.toGlobalVariable() },
                graph = graph
            ),
            processes = emptyList(),
            functions = emptyList()
        )
    }

    private fun saveGraphModel(graphState: GraphState) {
        project.saveBlueprint(
            json = json,
            blueprint = getBlueprint(graphState)
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