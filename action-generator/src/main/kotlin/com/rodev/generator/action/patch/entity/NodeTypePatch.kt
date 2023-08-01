package com.rodev.generator.action.patch.entity

import com.rodev.generator.action.patch.Patch
import kotlinx.serialization.Serializable

@Serializable
data class NodeTypePatch(
    override val id: String,
    override val remove: Boolean = false,
    val color: String
) : Patch()
