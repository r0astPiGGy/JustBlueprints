package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.rodev.jbpkmp.defaultViewPortViewModel
import com.rodev.jbpkmp.domain.model.Blueprint
import com.rodev.jbpkmp.domain.model.Project
import com.rodev.jbpkmp.domain.model.graph.EventGraph
import com.rodev.jbpkmp.domain.model.loadBlueprint
import com.rodev.jbpkmp.domain.model.saveBlueprint
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json

class EditorScreenViewModel(
    projectPath: String
) {

    private val json = Json { prettyPrint = true }
    private val project: Project

    private var mutableCurrentGraph by mutableStateOf<GraphModel?>(null)
    val currentGraph: GraphModel?
        get() = mutableCurrentGraph

    private var mutableState by mutableStateOf(EditorScreenState(isLoading = true))
    val state: EditorScreenState
        get() = mutableState

    private var loadingJob: Job? = null

    init {
        project = Project.loadFromFolder(projectPath)
        // load current graph
        loadingJob = CoroutineScope(Dispatchers.Default).launch {
            val blueprint = project.loadBlueprint()

            delay(2000)

            mutableCurrentGraph = GraphModel(
                name = "Event Graph",
                viewModel = defaultViewPortViewModel().apply {
                    load(blueprint.eventGraph.graph)
                }
            )
            updateState { it.copy(
                isLoading = false
            ) }
            loadingJob = null
        }
    }

    private fun updateState(scope: (EditorScreenState) -> EditorScreenState) {
        mutableState = scope(mutableState)
    }

    fun onEvent(event: EditorScreenEvent) {
        when (event) {
            is EditorScreenEvent.BuildProject -> {

            }

            is EditorScreenEvent.SaveProject -> {
                currentGraph?.let(::saveGraphModel)
            }
        }
    }

    fun onDispose() {
        onEvent(EditorScreenEvent.SaveProject)
        loadingJob?.cancel()
        loadingJob = null
    }

    private fun saveGraphModel(graphModel: GraphModel) {
        val graph = graphModel.viewModel.save()

        project.saveBlueprint(
            json = json,
            blueprint = Blueprint(
                eventGraph = EventGraph(
                    localVariables = emptyList(),
                    globalVariables = emptyList(),
                    graph = graph
                ),
                processes = emptyList(),
                functions = emptyList()
            )
        )
    }
}