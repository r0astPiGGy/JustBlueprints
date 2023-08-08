package com.rodev.jmcc_extractor.entity

import kotlinx.serialization.Serializable

// TODO interpret actions and add custom data (ContainerExtraData or smth)
// TODO Automatically add output exec pins if it's container
@Serializable
data class RawActionData(
    val id: String,
    val category: String,
    val subcategory: String? = null,
    val args: List<RawArgument>,
    val additionalInfo: List<String>? = null,
    val worksWith: List<String>? = null,
    val type: String
)

fun RawActionData.getArgumentById(id: String): RawArgument? {
    return args.find { it.id == id }
}
