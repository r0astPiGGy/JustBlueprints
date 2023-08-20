package com.rodev.jbpkmp.presentation.screens.editor_screen

import com.rodev.jbpkmp.domain.model.graph.GraphEntity

interface GraphStateFactory {

    fun createEventGraph(graph: GraphEntity): GraphState

    fun createProcessGraph(): GraphState

    fun createProcessGraph(graph: GraphEntity): GraphState

    fun createFunctionGraph(): GraphState

    fun createFunctionGraph(graph: GraphEntity): GraphState

}