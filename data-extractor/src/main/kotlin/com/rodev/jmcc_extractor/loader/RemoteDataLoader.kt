package com.rodev.jmcc_extractor.loader

import java.io.InputStream
import java.net.URL

open class RemoteDataLoader(
    url: String
) {
    private val url: URL = URL(url)

    fun openInputStream(): InputStream {
        return url.openStream()
    }
}
