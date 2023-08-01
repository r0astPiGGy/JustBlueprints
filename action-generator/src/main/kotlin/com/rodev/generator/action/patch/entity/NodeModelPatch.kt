package com.rodev.generator.action.patch.entity

import com.rodev.generator.action.entity.extra_data.ExtraData
import com.rodev.generator.action.patch.Patch
import com.rodev.generator.action.patch.Patchable
import kotlinx.serialization.Serializable

@Serializable
data class NodeModelPatch(
    override val id: String,
    override val remove: Boolean = false,
    @field:Patchable val type: String? = null,
    @field:Patchable val extra: ExtraData? = null,
    val input: List<PinModelPatch>? = null,
    val output: List<PinModelPatch>? = null
) : Patch()
