package com.rodev.jbpkmp.data

import androidx.compose.ui.res.useResource
import com.rodev.generator.action.entity.NodeModel
import com.rodev.jbpkmp.domain.source.NodeDataSource
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

@OptIn(ExperimentalSerializationApi::class)
class NodeDataSourceImpl(
    json: Json
) : NodeDataSource {

    private val nodeModels = mutableMapOf<String, NodeModel>()

    init {
        useResource<List<NodeModel>>(
            "data/node-models.json",
            json::decodeFromStream
        ).forEach { nodeModels[it.id] = it }
    }

    override fun getNodeModelById(id: String) = nodeModels[id]


}