package com.rodev.jbpkmp.domain.model

import com.rodev.generator.action.entity.SelectorType
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class SelectorGroup(
    val type: SelectorType,
    private val selectors: Map<String, String>
) {

    @Transient
    val selectorList = selectors.map { Selector(it.key, it.value) }

}

@Serializable
data class Selector(
    val id: String,
    val name: String
)
