package com.rodev.generator.action.patch

import com.rodev.generator.action.utils.Resources
import com.rodev.generator.action.utils.toMap
import com.rodev.jmcc_extractor.loader.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream

interface Patcher<T> {

    fun patch(objects: List<T>): List<T> {
        return objects.filterNot(::shouldRemove).mapNotNull(::patch)
    }

    fun patch(obj: T): T?

    fun shouldRemove(obj: T): Boolean {
        return false
    }

}

typealias PatchFunction<T, P> = (T, P) -> T?

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified P : Patch, T> createPatcher(
    patchListResource: String,
    noinline idExtractor: (T) -> String,
    noinline patchFunction: PatchFunction<T, P>
): Patcher<T> {
    val inputStream = Resources.loadResource(patchListResource)
    val patches: List<P> = json.decodeFromStream(inputStream)

    return DefaultPatcher(
        patches.toMap(Patch::id),
        idExtractor,
        patchFunction
    )
}

inline fun <reified P : Patch, T> List<T>.applyPatcher(
    patchesListResource: String,
    noinline idExtractor: (T) -> String,
    noinline patchFunction: PatchFunction<T, P>
): List<T> {
    return this.applyPatches(createPatcher<P, T>(patchesListResource, idExtractor, patchFunction))
}

fun <T> List<T>.applyPatches(patcher: Patcher<T>): List<T> {
    return patcher.patch(this)
}

class DefaultPatcher<T, P : Patch>(
    private val patches: Map<String, P>,
    private val idExtractor: (T) -> String,
    private val patchFunction: PatchFunction<T, P>
) : Patcher<T> {

    override fun patch(obj: T): T? {
        val patch = obj.getPatch() ?: return obj

        return patchFunction(obj, patch)
    }

    private fun T.getPatch(): P? {
        val id = idExtractor(this)
        return patches[id]
    }

    override fun shouldRemove(obj: T): Boolean {
        val patch = obj.getPatch() ?: return false

        return patch.remove
    }

}