package com.rodev.generator.action.patch.entity

import com.rodev.generator.action.patch.Patch
import com.rodev.generator.action.patch.Patchable
import kotlinx.serialization.Serializable

@Serializable
data class CategoryPatch(
    val path: String,
    override val remove: Boolean = false,
    @field:Patchable val name: String? = null
) : Patch() {

    override val id: String
        get() = path

}