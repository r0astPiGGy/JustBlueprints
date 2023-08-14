package com.rodev.jbpkmp.domain.repository

import com.rodev.generator.action.entity.Action
import com.rodev.generator.action.entity.Category

interface ActionDataSource {

    fun getAllActions(): List<Action>

    fun <T> transformActions(
        rootTransformFunction: (Category, List<T>) -> T,
        leafTransformFunction: (Action) -> T,
        filter: (Action) -> Boolean = { true }
    ): List<T>

    fun getActionById(id: String): Action

}