package com.rodev.generator.action.patch.entity

import com.rodev.generator.action.patch.Patch
import com.rodev.generator.action.utils.ColorUtil
import kotlinx.serialization.Serializable

@Serializable
data class PinTypePatch(
    override val id: String,
    override val remove: Boolean = false,
    val color: String
) : Patch()
