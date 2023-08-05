package com.rodev.generator.action.entity.extra_data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("compound_extra_data")
data class CompoundExtraData(
    val extras: List<ExtraData>
) : ExtraData() {

    inline fun <reified T : ExtraData> getExtraDataTypeOf(): T? {
        extras.forEach {
            if (it is T) return it
        }
        return null
    }

    inline fun <reified T : ExtraData> containsExtraDataOfType(): Boolean {
        return getExtraDataTypeOf<T>() != null
    }

}

interface CompoundExtraDataBuilderScope {

    fun add(extraData: ExtraData)

}

fun buildCompoundExtraData(scope: CompoundExtraDataBuilderScope.() -> Unit): CompoundExtraData {
    return with(CompoundExtraDataBuilderScopeImpl()) {
        scope()
        build()
    }
}

private class CompoundExtraDataBuilderScopeImpl : CompoundExtraDataBuilderScope {

    private val extras = mutableListOf<ExtraData>()

    override fun add(extraData: ExtraData) {
        extras.add(extraData)
    }

    fun build(): CompoundExtraData {
        return CompoundExtraData(
            extras
        )
    }

}
