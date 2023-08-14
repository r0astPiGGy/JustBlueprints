package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.*
import com.rodev.jbpkmp.domain.model.*
import com.rodev.jbpkmp.domain.model.graph.EventGraph
import com.rodev.jbpkmp.domain.model.variable.GlobalVariable
import com.rodev.jbpkmp.domain.model.variable.LocalVariable
import com.rodev.jbpkmp.domain.repository.ProgramDataRepository
import com.rodev.jbpkmp.domain.repository.update
import com.rodev.jbpkmp.domain.usecase.upload.BlueprintCompileUseCase
import com.rodev.jbpkmp.domain.usecase.upload.CodeUploadException
import com.rodev.jbpkmp.domain.usecase.upload.CodeUploadUseCase
import com.rodev.jbpkmp.presentation.screens.editor_screen.SelectionHandler.Default.resetSelection
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.CreateVariableGraphEvent
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.NodeAddAtCursorEvent
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.ViewPortViewModelFactory
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node.VariableNodeDisplay
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.model.Node
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class EditorScreenViewModel(
    projectPath: String,
    private val json: Json,
    private val repository: ProgramDataRepository,
    private val compile: BlueprintCompileUseCase,
    private val uploadCode: CodeUploadUseCase,
    private val selectionDispatcher: SelectionDispatcher,
    private val viewPortViewModelFactory: ViewPortViewModelFactory,
    private val dynamicVariableStateProvider: DynamicVariableStateProvider
) {

    lateinit var project: Project

    var currentGraph by mutableStateOf<GraphState?>(null)
        private set

    private var buildJob: Job? = null
    private val selectionActionVisitor: SelectionActionVisitor = SelectionActionVisitorImpl()

    private val clipboardActionVisitor: ClipboardActionVisitor = ClipboardActionVisitorImpl()
    private var clipboardEntry: ClipboardEntry? = null

    private val selectionHandler = SelectionHandlerImpl()
    val selectable by derivedStateOf {
        selectionHandler.selectable
    }

    private val variableStateProvider = VariableStateProviderImpl()

    val state = EditorScreenState(
        forceCodeLoad = repository.load().settings.forceCodeLoad
    )

    init {
        selectionDispatcher.registerSelectionHandler(selectionHandler)
        dynamicVariableStateProvider.setVariableStateProvider(variableStateProvider)

        load(projectPath)
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

                    variableStateProvider.addVariable(variable)
                    it.variables.add(variable)
                }
            }

            is EditorScreenEvent.AddGlobalVariable -> {
                event.variable.let {
                    variableStateProvider.addVariable(it)
                    state.variables.add(it)
                }
            }

            is EditorScreenEvent.OnDragAndDrop -> {
                handleDragAndDropEvent(event.variable, event.position)
            }

            EditorScreenEvent.CloseProject -> {
                repository.update {
                    lastOpenProjectPath = null
                }
                state.navigationResult = NavigationResult.GoBack
            }

            EditorScreenEvent.OpenSettingsScreen -> {
                state.showSettingsScreen = true
            }

            EditorScreenEvent.CloseSettingsScreen -> {
                state.showSettingsScreen = false
            }
        }
    }

    private fun load(projectPath: String) {
        project = Project.loadFromFolder(projectPath)

        val blueprint = project.loadBlueprint()
        val eventGraph = blueprint.eventGraph

        val localVariables = eventGraph.localVariables.map { it.toState() }
        val globalVariables = eventGraph.globalVariables.map { it.toState() }

        localVariables.forEach(variableStateProvider::addVariable)
        globalVariables.forEach(variableStateProvider::addVariable)

        val viewModel = viewPortViewModelFactory.create()
        viewModel.load(eventGraph.graph)

        currentGraph = GraphState(
            viewModel = viewModel,
            variables = localVariables
        )

        state.variables.addAll(
            globalVariables
        )
    }

    fun onDispose() {
        resetSelection()
        onEvent(EditorScreenEvent.SaveProject)
        repository.update {
            settings.forceCodeLoad = state.forceCodeLoad
        }
        buildJob?.cancel()
        buildJob = null
        dynamicVariableStateProvider.unregister()
        selectionDispatcher.unregisterSelectionHandler(selectionHandler)
    }

    @OptIn(ExperimentalComposeUiApi::class)
    fun handleKeyEvent(keyEvent: KeyEvent): Boolean {
        if (keyEvent.type != KeyEventType.KeyUp) return false

        if (keyEvent.key == Key.Delete) {
            return handleDeleteEvent()
        }

        if (keyEvent.key == Key.C && keyEvent.isCtrlPressed) {
            return handleCopyEvent()
        }

        if (keyEvent.key == Key.V && keyEvent.isCtrlPressed) {
            return handlePasteEvent()
        }

        return false
    }

    private fun handleCopyEvent(): Boolean {
        val selectable = this.selectable ?: return false

        clipboardEntry = selectable.asClipboardEntry()

        return true
    }

    private fun handlePasteEvent(): Boolean {
        val clipboardEntry = this.clipboardEntry ?: return false

        clipboardEntry.onPaste(clipboardActionVisitor)

        return true
    }

    private fun handleDeleteEvent(): Boolean {
        val selectable = this.selectable
        resetSelection()

        selectable?.let {
            if (it.isClipboardEntryOwner(clipboardEntry)) {
                this.clipboardEntry = null
            }

            it.onDelete(selectionActionVisitor)
        }

        return true
    }

    private fun Selectable.isClipboardEntryOwner(clipboardEntry: ClipboardEntry?): Boolean {
        clipboardEntry ?: return false

        return isClipboardEntryOwner(clipboardEntry)
    }

    private fun pasteNode(node: Node) {
        val graph = currentGraph ?: return

        graph.viewModel.onEvent(NodeAddAtCursorEvent(node))
    }

    private fun pasteLocalVariable(variable: LocalVariable) {
        onEvent(
            EditorScreenEvent.AddLocalVariable(
                variable.toState()
            )
        )
    }

    private fun pasteGlobalVariable(variable: GlobalVariable) {
        onEvent(
            EditorScreenEvent.AddGlobalVariable(
                variable.toState()
            )
        )
    }

    private fun deleteNode(nodeState: NodeState) {
        currentGraph?.viewModel?.deleteNode(nodeState)
    }

    private fun deleteLocalVariable(variable: LocalVariableState) {
        currentGraph?.let { graph ->
            graph.removeVariable(variable)
            graph.variables.removeIf { it.id == variable.id }
            variableStateProvider.removeVariable(variable)
        }
    }

    private fun deleteGlobalVariable(variable: GlobalVariableState) {
        currentGraph?.let { graph ->
            graph.removeVariable(variable)
            state.variables.removeIf { it.id == variable.id }
            variableStateProvider.removeVariable(variable)
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

    private fun handleDragAndDropEvent(variableState: VariableState, position: Offset) {
        val currentGraph = currentGraph ?: return
        val targetPosition = currentGraph.viewModel.scrollOffset + position

        currentGraph.viewModel.onEvent(
            CreateVariableGraphEvent(variableState, targetPosition)
        )
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
            // TODO handle BlueprintCompileException, outline the error node
            compile(blueprint)
        } catch (e: Exception) {
            state.result = EditorScreenResult.Error(
                stage = LoadingState.COMPILE,
                message = e.message,
                stackTrace = e.stackTraceToString()
            )
            return
        }

        state.result = EditorScreenResult.Loading(state = LoadingState.UPLOAD)

        project.writeCompileOutput(data)

        try {
            state.result = EditorScreenResult.SuccessUpload(
                uploadCommand = uploadCode(data)
            )
        } catch (e: CodeUploadException) {
            state.result = EditorScreenResult.Error(
                stage = LoadingState.UPLOAD,
                message = e.message,
                stackTrace = e.stackTraceToString()
            )
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

    private inner class ClipboardActionVisitorImpl : ClipboardActionVisitor {
        override fun pasteNode(nodeState: Node) {
            this@EditorScreenViewModel.pasteNode(nodeState)
        }

        override fun pasteLocalVariable(variable: LocalVariable) {
            this@EditorScreenViewModel.pasteLocalVariable(variable)
        }

        override fun pasteGlobalVariable(variable: GlobalVariable) {
            this@EditorScreenViewModel.pasteGlobalVariable(variable)
        }
    }

    private inner class VariableStateProviderImpl : VariableStateProvider {

        private val variablesById = hashMapOf<String, VariableState>()

        override fun getVariableStateById(id: String): VariableState? {
            return variablesById[id]
        }

        fun addVariable(variable: VariableState) {
            variablesById[variable.id] = variable
        }

        fun removeVariable(variable: VariableState) {
            variablesById.remove(variable.id)
        }

    }

    private class SelectionHandlerImpl : SelectionHandler {

        var selectable: Selectable? by mutableStateOf(null)

        override fun onSelect(selectable: Selectable) {
            this.selectable?.let {
                it.selected = false
            }
            this.selectable = selectable
            selectable.selected = true
        }

        override fun resetSelection() {
            selectable?.let {
                it.selected = false
            }
            selectable = null
        }

    }
}