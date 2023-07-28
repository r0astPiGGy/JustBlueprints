package com.rodev.jbpkmp.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.useResource
import com.rodev.generator.action.entity.Action
import com.rodev.generator.action.entity.Category
import com.rodev.generator.action.entity.NodeModel
import com.rodev.jbpkmp.domain.repository.NodeDataSource
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

private val json = Json

object GlobalDataSource : NodeDataSource {

    private val mutableActions = mutableListOf<Action>()
    val actions: List<Action>
        get() = mutableActions

    private val mutableCategories = mutableListOf<Category>()
    val categories: List<Category>
        get() = mutableCategories

    private val mutableIcons = mutableMapOf<String, Painter>()

    private val mutableNodeModels = mutableMapOf<String, NodeModel>()

    @OptIn(ExperimentalSerializationApi::class)
    fun load() {
        mutableActions.clear()
        mutableNodeModels.clear()
        mutableCategories.clear()

        useResource<List<Action>>("data/actions.json", json::decodeFromStream).let(mutableActions::addAll)
        useResource<List<Category>>("data/categories.json", json::decodeFromStream).let(mutableCategories::addAll)
        useResource<List<NodeModel>>("data/node-models.json", json::decodeFromStream).forEach { mutableNodeModels[it.id] = it }

    }

    fun getIconById(id: String): Painter {
        val painter = mutableIcons[id]

        if (painter != null) return painter

        println("Icon by id $id not found. Using default one")

        return ColorPainter(Color.White)
    }

    override fun getNodeModelById(id: String): NodeModel {
        return mutableNodeModels[id]!!
    }

}