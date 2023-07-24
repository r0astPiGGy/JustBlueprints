package com.rodev.jbpkmp.domain.repository

import com.rodev.jbpkmp.domain.model.Action
import com.rodev.jbpkmp.domain.model.Category
import com.rodev.nodeui.model.Node

interface ActionDataSource {

    fun getNodeById(id: String): Node

    fun <T> getActions(
        rootTransformFunction: (Category, List<T>) -> T,
        leafTransformFunction: (Action) -> T
    ): List<T>

}