package com.rodev.jbpkmp.data

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
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

    private val mutableIcons = mutableMapOf<String, ImageBitmap>()

    private val mutableNodeModels = mutableMapOf<String, NodeModel>()

    @OptIn(ExperimentalSerializationApi::class)
    fun load() {
        mutableActions.clear()
        mutableNodeModels.clear()
        mutableCategories.clear()
        mutableIcons.clear()

        useResource<List<Action>>("data/actions.json", json::decodeFromStream).let(mutableActions::addAll)
        useResource<List<Category>>("data/categories.json", json::decodeFromStream).let(mutableCategories::addAll)
        useResource<List<NodeModel>>("data/node-models.json", json::decodeFromStream).forEach { mutableNodeModels[it.id] = it }

        mutableActions.forEach {
            loadIconByPath(it.iconPath)
        }
    }

    private fun loadIconByPath(path: String) {
        try {
            mutableIcons[path] = useResource(path, ::loadImageBitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getIconById(id: String) = mutableIcons[id]

    override fun getNodeModelById(id: String) = mutableNodeModels[id]!!

}

val Action.iconPath: String
    get() {
        return "images/icons/$iconNamespace/$id.png"
    }