package com.rodev.generator.action.utils

import java.io.InputStream

object Resources {
    fun loadResource(path: String): InputStream {
        val contextClassLoader = Thread.currentThread().contextClassLoader!!
        val resource = contextClassLoader.getResourceAsStream(path)
            ?: (this.javaClass).getResourceAsStream(path)
        return requireNotNull(resource) { "Resource $path not found" }
    }
}

