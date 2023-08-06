package com.rodev.generator.action.entity

import com.rodev.generator.action.entity.extra_data.ExtraData
import com.rodev.generator.action.utils.toMap
import kotlinx.serialization.Serializable

@Serializable
data class NodeCompound(
    val id: String,
    val type: String,
    val name: String,
    val input: List<PinModel>,
    val output: List<PinModel>,
    val iconPath: String,
    val category: String,
    val details: ActionDetails,
    val extra: ExtraData? = null,
)

fun NodeCompound.toNodeModel(): NodeModel {
    return NodeModel(
        id = id,
        type = type,
        input = input,
        output = output,
        extra = extra
    )
}

fun NodeCompound.toAction(): Action {
    return Action(
        id = id,
        name = name,
        input = extractTypes(input),
        output = extractTypes(output),
        iconPath = iconPath,
        category = category
    )
}

fun NodeCompound.toActionDetails(): ActionDetails {
    return details.copy(id = id, name = name)
}

fun extractTypes(pinModels: List<PinModel>): Set<String> {
    return pinModels.map { it.type }.toSet()
}
