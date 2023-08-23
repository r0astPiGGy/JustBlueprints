package com.rodev.jmcc_extractor.loader

import java.io.InputStream
import java.io.InputStreamReader
import java.util.Properties

typealias InputStreamProvider = () -> InputStream

class PropertyLoader(
    private val inputStreamProvider: InputStreamProvider
) : DataLoader<Map<String, String>> {

    override fun load(): Map<String, String> {
        return inputStreamProvider().use {
            Properties().run {
                load(InputStreamReader(it))
                stringPropertyNames().associateWith(::getProperty)
            }
        }
    }

}

fun RemoteDataLoader.asPropertyLoader(): DataLoader<Map<String, String>> {
    return PropertyLoader { this.openInputStream() }
}