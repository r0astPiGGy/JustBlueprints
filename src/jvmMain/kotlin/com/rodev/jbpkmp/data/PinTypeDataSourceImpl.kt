package com.rodev.jbpkmp.data

import androidx.compose.ui.res.useResource
import com.rodev.generator.action.entity.PinType
import com.rodev.jbpkmp.domain.repository.PinTypeDataSource
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

@OptIn(ExperimentalSerializationApi::class)
class PinTypeDataSourceImpl(
    json: Json
) : PinTypeDataSource {

    private val pinTypes = mutableMapOf<String, PinType>()

    init {
        useResource<List<PinType>>(
            "data/pin-types.json",
            json::decodeFromStream
        ).forEach { pinTypes[it.id] = it }
    }

    override fun getPinTypeById(id: String) = pinTypes[id]
}