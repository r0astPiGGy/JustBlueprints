package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.*
import com.rodev.jbpkmp.domain.model.*
import com.rodev.jbpkmp.domain.model.graph.FunctionGraph
import com.rodev.jbpkmp.domain.model.variable.GlobalVariable
import com.rodev.jbpkmp.domain.model.variable.LocalVariable
import com.rodev.jbpkmp.domain.repository.ProgramDataRepository
import com.rodev.jbpkmp.domain.repository.LocalProjectReference
import com.rodev.jbpkmp.domain.repository.update
import com.rodev.jbpkmp.domain.repository.updateEditorData
import com.rodev.jbpkmp.domain.usecase.upload.BlueprintCompileUseCase
import com.rodev.jbpkmp.domain.usecase.upload.CodeUploadException
import com.rodev.jbpkmp.domain.usecase.upload.CodeUploadUseCase
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.InvokableTab
import com.rodev.jbpkmp.presentation.components.TabLayoutHostState
import com.rodev.jbpkmp.presentation.components.TabState
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.OverviewPanelState
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.CreateFunctionGraphEvent
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.CreateProcessGraphEvent
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.CreateVariableGraphEvent
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.NodeAddAtCursorEvent
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node.InvokableReferenceDisplay
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node.VariableNodeDisplay
import com.rodev.jbpkmp.util.generateUniqueId
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.model.Node
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

typealias EditorTabLayoutState = TabLayoutHostState<GraphState>

typealias EditorTabState = TabState<GraphState>

class EditorScreenViewModel(
    private val projectReference: LocalProjectReference,
    private val repository: ProgramDataRepository,
    private val selectionDispatcher: SelectionDispatcher,
    private val graphStateFactory: GraphStateFactory,
    private val dynamicVariableStateProvider: DynamicVariableStateProvider,
    private val dynamicInvokableReferenceProvider: DynamicInvokableReferenceProvider,
    private val uploadCode: CodeUploadUseCase,
    private val compile: BlueprintCompileUseCase
) {

    val projectName = projectReference.project.name

    val tabLayoutHostState = EditorTabLayoutState()
    val currentGraph by derivedStateOf {
        tabLayoutHostState.currentTab?.data
    }
    val state = EditorScreenState()
    val overviewPanelState = OverviewPanelState()

    private var buildJob: Job? = null

    private var clipboardEntry: ClipboardEntry? = null
    private val clipboardActionVisitor: ClipboardActionVisitor = ClipboardActionVisitorImpl()

    private val selectionHandler = SelectionHandlerImpl()
    private val selectionActionVisitor: SelectionActionVisitor = SelectionActionVisitorImpl()
    val selectable by derivedStateOf {
        selectionHandler.selectable
    }

    private val variableStateProvider = VariableStateProviderImpl()
    private val invokableReferenceProvider = InvokableReferenceProviderImpl()

    private val blueprintState: BlueprintState

    val contextMenuActionHandler: ContextMenuActionHandler = ContextMenuActionHandlerImpl()

    init {
        state.forceCodeLoad = repository.load().settings.forceCodeLoad

        selectionDispatcher.registerSelectionHandler(selectionHandler)
        dynamicVariableStateProvider.setVariableStateProvider(variableStateProvider)
        dynamicInvokableReferenceProvider.setInvokableReferenceProvider(invokableReferenceProvider)

        blueprintState = load()
    }

    val globalVariables: List<GlobalVariableState> by derivedStateOf {
        blueprintState.globalVariables
    }

    val processes: List<ProcessState> by derivedStateOf { 
        blueprintState.processGraphs
    }

    val functions: List<FunctionState> by derivedStateOf {
        blueprintState.functionGraphs
    }

    init {
        restoreTabs()
    }

    fun onEvent(event: EditorScreenEvent) {
        when (event) {
            is EditorScreenEvent.BuildProject -> {
                buildProject()
            }

            is EditorScreenEvent.SaveProject -> {
                projectReference.updateEditorData {
                    saveTabs()
                }
                projectReference.blueprint.save(
                    blueprint = blueprintState.toBlueprint()
                )
            }

            is EditorScreenEvent.AddLocalVariable -> {
                currentGraph?.let {
                    val variable = LocalVariableState(name = event.name)

                    variableStateProvider.addVariable(variable)
                    it.variables.add(variable)
                }
            }

            is EditorScreenEvent.AddGlobalVariable -> {
                GlobalVariableState(
                    name = event.name,
                    type = event.type
                ).let {
                    variableStateProvider.addVariable(it)
                    blueprintState.globalVariables.add(it)
                }
            }

            is EditorScreenEvent.AddProcess -> {
                val graphState = graphStateFactory.createProcessGraph()
                val process = ProcessState(
                    reference = invokableReferenceProvider.createAndAdd(
                        id = graphState.id,
                        name = event.name
                    ),
                    graphState = graphState
                )
                blueprintState.processGraphs.add(process)

                tabLayoutHostState.openTab(
                    tabState = InvokableTab(process)
                )
            }

            is EditorScreenEvent.AddFunction -> {
                val graphState = graphStateFactory.createFunctionGraph()
                val function = FunctionState(
                    reference = invokableReferenceProvider.createAndAdd(
                        id = graphState.id,
                        name = event.name
                    ),
                    graphState = graphState
                )
                blueprintState.functionGraphs.add(function)

                tabLayoutHostState.openTab(
                    tabState = InvokableTab(function)
                )
            }

            is EditorScreenEvent.OnDragAndDrop -> {
                val target = event.target
                val position = event.position

                val graphEvent = when (target) {
                    is VariableState -> CreateVariableGraphEvent(target, position)
                    is ProcessState -> CreateProcessGraphEvent(target, position)
                    is FunctionState -> CreateFunctionGraphEvent(target, position)
                    else -> throw IllegalStateException("Unknown drag and drop target")
                }

                currentGraph?.viewModel?.onEvent(graphEvent)
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

            is EditorScreenEvent.OpenFunction -> {
                tabLayoutHostState.openTab(
                    InvokableTab(event.function)
                )
            }

            is EditorScreenEvent.OpenProcess -> {
                tabLayoutHostState.openTab(
                    InvokableTab(event.process)
                )
            }

            is EditorScreenEvent.OnFunctionRename -> {
                event.function.reference.name = event.name
            }

            is EditorScreenEvent.OnProcessRename -> {
                event.process.reference.name = event.name
            }
        }
    }

    private fun load(): BlueprintState {
        val blueprint = try {
            projectReference.blueprint.load()
        } catch (e: Exception) {
            state.result = EditorScreenResult.RuntimeError(
                message = e.message,
                stackTrace = e.stackTraceToString()
            )
            emptyBlueprint()
        }

        listOf(blueprint.functions, blueprint.processes).forEach { list ->
            list.forEach {
                invokableReferenceProvider.createAndAdd(
                    id = it.id,
                    name = it.name
                )
            }
        }

        blueprint.localVariables.map { it.toState() }.forEach(variableStateProvider::addVariable)
        val variables = blueprint.globalVariables.map { it.toState() }.also(variableStateProvider::addVariables)

        return BlueprintState(
            eventGraph = graphStateFactory.createEventGraph(blueprint.eventGraph),
            functions = loadFunctions(blueprint.functions),
            processes = loadProcesses(blueprint.processes),
            variables = variables
        )
    }

    private fun saveTabs(): EditorData {
        return EditorData(
            selectedTabId = currentGraph!!.id,
            openedTabs = tabLayoutHostState.tabs.map { tab ->
                val id = tab.data.id
                if (id == blueprintState.eventGraph.id) {
                    EventGraphTab(id)
                } else {
                    InvokableGraphTab(id)
                }
            }.toSet()
        )
    }

    private fun restoreTabs() {
        val editorData = projectReference.project.editorData
        val eventGraph = blueprintState.eventGraph

        if (editorData == null) {
            tabLayoutHostState.addTab(
                EditorTabState(
                    name = "Event Graph",
                    closeable = false,
                    data = eventGraph
                )
            )
            return
        }

        val selectedTabId = editorData.selectedTabId
        var selectedGraphState: GraphState? = null

        editorData.openedTabs.forEach { tab ->
            when (tab) {
                is EventGraphTab -> {
                    tabLayoutHostState.addTab(
                        EditorTabState(
                            name = "Event Graph",
                            closeable = false,
                            data = eventGraph
                        )
                    )
                    if (selectedTabId == eventGraph.id) {
                        selectedGraphState = eventGraph
                    }
                }
                is InvokableGraphTab -> {
                    val invokableState = blueprintState.findGraphById(tab.id)

                    tabLayoutHostState.addTab(
                        InvokableTab(
                            invokableState = invokableState
                        )
                    )
                    if (selectedTabId == invokableState.id) {
                        selectedGraphState = invokableState.graphState
                    }
                }
            }

            tabLayoutHostState.openTabIf { it.data == selectedGraphState }
        }
    }

    private fun loadFunctions(functions: List<FunctionGraph>): List<FunctionState> {
        return functions.map { graph ->
            FunctionState(
                reference = invokableReferenceProvider.getInvokableReferenceById(graph.id)!!,
                graphState = graphStateFactory.createFunctionGraph(graph)
            )
        }
    }

    private fun loadProcesses(processes: List<FunctionGraph>): List<ProcessState> {
        return processes.map { graph ->
            ProcessState(
                reference = invokableReferenceProvider.getInvokableReferenceById(graph.id)!!,
                graphState = graphStateFactory.createProcessGraph(graph)
            )
        }
    }

    fun onDispose() {
        selectionHandler.resetSelection()
        onEvent(EditorScreenEvent.SaveProject)
        repository.update {
            settings.forceCodeLoad = state.forceCodeLoad
        }
        buildJob?.cancel()
        buildJob = null
        dynamicVariableStateProvider.unregister()
        dynamicInvokableReferenceProvider.unregister()
        selectionDispatcher.unregisterSelectionHandler(selectionHandler)
    }

    @OptIn(ExperimentalComposeUiApi::class)
    fun handleKeyEvent(keyEvent: KeyEvent): Boolean {
        if (keyEvent.key == Key.DirectionLeft && keyEvent.isAltPressed && keyEvent.type == KeyEventType.KeyDown) {
            return handleLeftTabScroll()
        }

        if (keyEvent.key == Key.DirectionRight && keyEvent.isAltPressed && keyEvent.type == KeyEventType.KeyDown) {
            return handleRightTabScroll()
        }

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

    private fun handleLeftTabScroll(): Boolean {
        scrollTabBy(-1)
        return true
    }

    private fun handleRightTabScroll(): Boolean {
        scrollTabBy(1)
        return true
    }

    private fun scrollTabBy(i: Int) {
        val tabSize = tabLayoutHostState.tabs.size - 1
        if (tabSize == 0) return

        val currentIndex = tabLayoutHostState.currentTabIndex
        var target = currentIndex + i

        target = when {
            target > tabSize -> 0
            target < 0 -> tabSize
            else -> target
        }

        tabLayoutHostState.openTab(target)
    }

    private fun handleFunctionRename(function: FunctionState) {
        overviewPanelState.onEvent(OverviewPanelState.Event.RenameFunction(function))
    }

    private fun handleProcessRename(process: ProcessState) {
        overviewPanelState.onEvent(OverviewPanelState.Event.RenameProcess(process))
    }

    private fun handleCopyEvent(): Boolean {
        val selectable = this.selectable ?: return false

        handleCopyEvent(selectable)

        return true
    }

    private fun handleCopyEvent(selectable: Selectable) {
        clipboardEntry = selectable.asClipboardEntry()
    }

    private fun handlePasteEvent(): Boolean {
        val clipboardEntry = this.clipboardEntry ?: return false

        clipboardEntry.onPaste(clipboardActionVisitor)

        return true
    }

    private fun handleDeleteEvent(): Boolean {
        val selectable = this.selectable
        selectionHandler.resetSelection()

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
                name = variable.name
            )
        )
    }

    private fun pasteGlobalVariable(variable: GlobalVariable) {
        onEvent(
            EditorScreenEvent.AddGlobalVariable(
                name = variable.name,
                type = variable.type
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
        blueprintState.forEachGraph { it.removeVariable(variable) }
        blueprintState.globalVariables.removeIf { it.id == variable.id }
        variableStateProvider.removeVariable(variable)
    }

    private fun deleteFunction(function: FunctionState) {
        blueprintState.functionGraphs.remove(function)
        deleteInvokable(function)
    }

    private fun deleteProcess(process: ProcessState) {
        blueprintState.processGraphs.remove(process)
        deleteInvokable(process)
    }

    private fun deleteInvokable(invokableState: InvokableState) {
        tabLayoutHostState.removeIf { it.data == invokableState.graphState }
        invokableState.graphState.variables.forEach(variableStateProvider::removeVariable)
        blueprintState.forEachGraph { graph ->
            graph.deleteNodesByPredicate {
                val display = it.nodeDisplay

                if (display is InvokableReferenceDisplay) {
                    return@deleteNodesByPredicate display.invokableId == invokableState.id
                }

                false
            }
        }
        invokableReferenceProvider.removeReference(invokableState.reference)
    }

    private fun GraphState.removeVariable(variable: VariableState) {
        deleteNodesByPredicate { node ->
            val representation = node.nodeDisplay
            if (representation is VariableNodeDisplay) {
                return@deleteNodesByPredicate representation.variableId == variable.id
            }

            false
        }
    }

    private fun GraphState.findNodes(predicate: (NodeState) -> Boolean): List<NodeState> {
        return viewModel.nodeStates.filter(predicate)
    }

    private fun GraphState.deleteNodesByPredicate(predicate: (NodeState) -> Boolean) {
        findNodes(predicate).let(viewModel::deleteNodes)
    }

    private fun buildProject() {
        val blueprint = blueprintState.toBlueprint()

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

        projectReference.writeData("output.json", data)

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

        override fun deleteFunction(function: FunctionState) {
            this@EditorScreenViewModel.deleteFunction(function)
        }

        override fun deleteProcess(processState: ProcessState) {
            this@EditorScreenViewModel.deleteProcess(processState)
        }
    }

    private inner class ContextMenuActionHandlerImpl : ContextMenuActionHandler {
        override fun onEvent(event: ContextMenuEvent) {
            when (event) {
                is ContextMenuEvent.CopyVariable -> handleCopyEvent(event.variable)
                is ContextMenuEvent.DeleteFunction -> deleteFunction(event.function)
                is ContextMenuEvent.DeleteProcess -> deleteProcess(event.process)
                is ContextMenuEvent.DeleteVariable -> {
                    when (val variable = event.variable) {
                        is GlobalVariableState -> deleteGlobalVariable(variable)
                        is LocalVariableState -> deleteLocalVariable(variable)
                    }
                }
                is ContextMenuEvent.PasteVariable -> handlePasteEvent()
                is ContextMenuEvent.RenameFunction -> handleFunctionRename(event.function)
                is ContextMenuEvent.RenameProcess -> handleProcessRename(event.process)
            }
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

        override fun pasteFunction(function: FunctionGraph) {
            // TODO
        }

        override fun pasteProcess(process: FunctionGraph) {
            // TODO
        }
    }

    private inner class InvokableReferenceProviderImpl : InvokableReferenceProvider {

        private val referencesById = hashMapOf<String, InvokableReference>()

        override fun getInvokableReferenceById(id: String): InvokableReference? {
            return referencesById[id]
        }

        fun addReference(reference: InvokableReference) {
            referencesById[reference.id] = reference
        }

        fun addReferences(references: List<InvokableReference>) {
            references.forEach(::addReference)
        }

        fun removeReference(reference: InvokableReference) {
            referencesById.remove(reference.id)
        }

        fun createAndAdd(id: String, name: String): InvokableReference {
            return InvokableReference(id, name).also {
                addReference(it)
            }
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

        fun addVariables(variables: List<VariableState>) {
            variables.forEach(::addVariable)
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