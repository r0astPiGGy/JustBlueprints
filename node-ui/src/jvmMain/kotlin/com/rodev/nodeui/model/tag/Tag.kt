package com.rodev.nodeui.model.tag

import kotlinx.serialization.Serializable

@Serializable
sealed interface Tag {

    val value: Any?

}

@Serializable
class StringTag(
    override val value: String?
) : Tag

@Serializable
class MapTag(
    override val value: Map<String, Tag> = hashMapOf()
) : Tag {

    fun getString(id: String): String? {
        val tag = value[id]

        if (tag is StringTag) {
            return tag.value
        }
        return null
    }

}

fun buildMapTag(scope: MapTagBuilderScope.() -> Unit): MapTag {
    return with(MapTagBuilderScopeImpl()) {
        scope()
        MapTag(map)
    }
}

fun inheritBuilder(mapTag: MapTag, scope: MapTagBuilderScope.() -> Unit): MapTag {
    return with(MapTagBuilderScopeImpl()) {
        map.putAll(mapTag.value)
        scope()
        MapTag(map)
    }
}

private class MapTagBuilderScopeImpl : MapTagBuilderScope {

    val map = hashMapOf<String, Tag>()

    override fun putString(id: String, string: String) {
        map[id] = StringTag(string)
    }
}

interface MapTagBuilderScope {

    fun putString(id: String, string: String)

}