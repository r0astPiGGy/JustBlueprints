package com.rodev.jmcc_extractor.loader

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

typealias InputStreamProvider = () -> InputStream

class PropertyLoader(
    private val inputStreamProvider: InputStreamProvider
) : DataLoader<Map<String, String>> {

    override fun load(): Map<String, String> {
        val map = HashMap<String, String>()

        fun readLine(line: String) {
            val values = line.split("=")
            if (values.size < 2) return

            map[values[0]] = values[1]
        }

        inputStreamProvider().use {
            BufferedReader(InputStreamReader(it))
                .lines()
                .forEach(::readLine)
        }

        return map
    }

}

fun RemoteDataLoader.asPropertyLoader(): DataLoader<Map<String, String>> {
    return PropertyLoader { this.openInputStream() }
}