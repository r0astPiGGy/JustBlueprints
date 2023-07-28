package com.rodev.jmcc_extractor.entity

import kotlinx.serialization.Serializable

@Serializable
data class RawActionData(
    val id: String,
    val category: String,
    val subcategory: String? = null,
    val args: List<RawArgument>
)

fun RawActionData.getArgumentById(id: String): RawArgument? {
    return args.find { it.id == id }
}
