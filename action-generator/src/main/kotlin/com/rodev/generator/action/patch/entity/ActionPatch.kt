package com.rodev.generator.action.patch.entity

import com.rodev.generator.action.patch.Patch
import com.rodev.generator.action.patch.Patchable
import kotlinx.serialization.Serializable

@Serializable
data class ActionPatch(
    override val id: String,
    override val remove: Boolean = false,
    @field:Patchable val name: String? = null,
    @field:Patchable val input: Set<String>? = null,
    @field:Patchable val output: Set<String>? = null,
    @field:Patchable val iconNamespace: String? = null,
    @field:Patchable val category: String? = null
) : Patch()