package com.rodev.jbpkmp.data

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import com.rodev.generator.action.entity.*
import com.rodev.generator.action.utils.ColorUtil
import com.rodev.jbpkmp.domain.model.SelectorGroup
import com.rodev.jbpkmp.domain.repository.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

private val json = Json

object GlobalDataSource : NodeDataSource, PinTypeDataSource, NodeTypeDataSource, ActionDataSource, SelectorDataSource {

    private val mutableActions = mutableListOf<Action>()
    private val mutableCategories = mutableListOf<Category>()
    private val mutableIcons = mutableMapOf<String, ImageBitmap>()
    private val mutableNodeModels = mutableMapOf<String, NodeModel>()
    private val mutablePinTypes = mutableMapOf<String, PinType>()
    private val mutableNodeTypes = mutableMapOf<String, NodeType>()
    private val selectors = mutableMapOf<SelectorType, SelectorGroup>()

    private lateinit var actionDataSource: ActionDataSource

    @OptIn(ExperimentalSerializationApi::class)
    fun load() {
        mutableActions.clear()
        mutableNodeModels.clear()
        mutableCategories.clear()
        mutableIcons.clear()
        mutablePinTypes.clear()
        mutableNodeTypes.clear()

        useResource<List<Action>>("data/actions.json", json::decodeFromStream).let(mutableActions::addAll)
        useResource<List<Category>>("data/categories.json", json::decodeFromStream).let(mutableCategories::addAll)
        useResource<List<NodeModel>>("data/node-models.json", json::decodeFromStream).forEach { mutableNodeModels[it.id] = it }
        useResource<List<PinType>>("data/pin-types.json", json::decodeFromStream).forEach { mutablePinTypes[it.id] = it }
        useResource<List<NodeType>>("data/node-types.json", json::decodeFromStream).forEach { mutableNodeTypes[it.id] = it }
        useResource<List<SelectorGroup>>("data/selectors.json", json::decodeFromStream).forEach { selectors[it.type] = it }

        mutableActions.forEach {
            loadIconByPath(it.iconPath)
        }

        actionDataSource = ActionDataSourceImpl(mutableActions, mutableCategories)
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

    override fun getPinTypeById(id: String): PinType? = mutablePinTypes[id]

    override fun getNodeTypeById(id: String): NodeType? = mutableNodeTypes[id]
    override fun <T> getActions(
        rootTransformFunction: (Category, List<T>) -> T,
        leafTransformFunction: (Action) -> T
    ): List<T> = actionDataSource.getActions(rootTransformFunction, leafTransformFunction)

    override fun getActionById(id: String): Action = actionDataSource.getActionById(id)

    override fun getSelectorByType(type: SelectorType) = selectors[type]!!

}