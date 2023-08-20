package com.rodev.jbpkmp.data

import androidx.compose.ui.res.useResource
import com.rodev.generator.action.entity.SelectorType
import com.rodev.jbpkmp.domain.model.SelectorGroup
import com.rodev.jbpkmp.domain.source.SelectorDataSource
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

@OptIn(ExperimentalSerializationApi::class)
class SelectorDataSourceImpl(
    json: Json
) : SelectorDataSource {

    private val selectors = mutableMapOf<SelectorType, SelectorGroup>()

    init {
        useResource<List<SelectorGroup>>(
            "data/selectors.json",
            json::decodeFromStream
        ).forEach { selectors[it.type] = it }
    }

    override fun getSelectorByType(type: SelectorType) = selectors[type]!!
}