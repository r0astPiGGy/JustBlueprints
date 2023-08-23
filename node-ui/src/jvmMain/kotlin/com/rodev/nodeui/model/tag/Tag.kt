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
class BooleanTag(
    override val value: Boolean?
) : Tag

@Serializable
class MapTag(
    override val value: Map<String, Tag> = hashMapOf()
) : Tag {

    private inline fun <reified T : Tag> tagOrNull(id: String): T? {
        return value[id] as? T
    }

    fun getString(id: String): String? {
        return tagOrNull<StringTag>(id)?.value
    }

    fun getBoolean(id: String): Boolean? {
        return tagOrNull<BooleanTag>(id)?.value
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

    override fun putBoolean(id: String, boolean: Boolean) {
        map[id] = BooleanTag(boolean)
    }
}

interface MapTagBuilderScope {

    fun putString(id: String, string: String)

    fun putBoolean(id: String, boolean: Boolean)

}