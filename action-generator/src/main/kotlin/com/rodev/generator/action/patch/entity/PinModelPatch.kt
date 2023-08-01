package com.rodev.generator.action.patch.entity

import com.rodev.generator.action.patch.Patch
import com.rodev.generator.action.patch.Patchable
import kotlinx.serialization.Serializable

@Serializable
data class PinModelPatch(
    override val id: String,
    override val remove: Boolean = false,
    @field:Patchable val type: String? = null,
    @field:Patchable val label: String? = null,
    @field:Patchable val extra: String? = null
) : Patch()