package com.rodev.jmcc_extractor.loader

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

private typealias JsonDataProvider<T> = () -> T

class JsonDataLoader<T>(
    private val jsonDataProvider: JsonDataProvider<T>
) : DataLoader<T> {

    override fun load(): T = jsonDataProvider()

}

private val privateJson = Json { ignoreUnknownKeys = true }

val json: Json
    get() = privateJson

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> RemoteDataLoader.asJsonDataLoader(): DataLoader<T> {
    return JsonDataLoader {
        json.decodeFromStream(openInputStream())
    }
}
