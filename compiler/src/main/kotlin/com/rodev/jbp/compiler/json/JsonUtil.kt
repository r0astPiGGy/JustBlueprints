package com.rodev.jbp.compiler.json

import kotlinx.serialization.json.*

fun <T : JsonElement> JsonObjectBuilder.put(key: String, elements: List<T>): JsonElement? {
    return putJsonArray(key) {
        addAll(elements)
    }
}

fun JsonObjectBuilder.putNotNull(key: String, jsonElement: JsonElement?): JsonElement? {
    return jsonElement?.let {
        put(key, it)
    }
}

fun JsonObjectBuilder.putNotNull(key: String, string: String?): JsonElement? {
    return string?.let {
        put(key, it)
    }
}

fun JsonObjectBuilder.putNotNull(key: String, number: Number?): JsonElement? {
    return number?.let {
        put(key, it)
    }
}

fun JsonObject.inheritBuilder(scope: JsonObjectBuilder.() -> Unit): JsonObject {
    return buildJsonObject {
        this@inheritBuilder.forEach(::put)
        scope()
    }
}