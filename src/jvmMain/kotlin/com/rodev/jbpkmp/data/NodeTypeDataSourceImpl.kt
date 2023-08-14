package com.rodev.jbpkmp.data

import androidx.compose.ui.res.useResource
import com.rodev.generator.action.entity.NodeType
import com.rodev.jbpkmp.domain.repository.NodeTypeDataSource
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

@OptIn(ExperimentalSerializationApi::class)
class NodeTypeDataSourceImpl(
    json: Json
) : NodeTypeDataSource {

    private val nodeTypes = mutableMapOf<String, NodeType>()

    init {
        useResource<List<NodeType>>(
            "data/node-types.json",
            json::decodeFromStream
        ).forEach { nodeTypes[it.id] = it }
    }

    override fun getNodeTypeById(id: String) = nodeTypes[id]
}