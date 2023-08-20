package com.rodev.jbpkmp.data

import androidx.compose.ui.res.useResource
import com.rodev.generator.action.entity.ActionDetails
import com.rodev.jbpkmp.domain.source.ActionDetailsDataSource
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

@OptIn(ExperimentalSerializationApi::class)
class ActionDetailsDataSourceImpl(
    json: Json
) : ActionDetailsDataSource {

    private val actionDetails = mutableMapOf<String, ActionDetails>()

    init {
        useResource<List<ActionDetails>>(
            "data/action-details.json",
            json::decodeFromStream
        ).forEach { actionDetails[it.id] = it }
    }

    override fun getActionDetailsById(id: String) = actionDetails[id]
}