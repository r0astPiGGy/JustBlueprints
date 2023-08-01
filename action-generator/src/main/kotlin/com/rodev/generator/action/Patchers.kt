package com.rodev.generator.action

import com.rodev.generator.action.entity.*
import com.rodev.generator.action.patch.Patch
import com.rodev.generator.action.patch.Patchable
import com.rodev.generator.action.patch.entity.*
import com.rodev.generator.action.utils.ColorUtil
import java.lang.reflect.Field
import java.util.*

fun patchNodeModel(nodeModel: NodeModel, patch: NodeModelPatch): NodeModel {
    patchFields(nodeModel, patch)

    return nodeModel.copy(
        input = patchPins(nodeModel.input, patch.input),
        output = patchPins(nodeModel.output, patch.output)
    )
}

private fun patchPins(
    targetEntities: List<PinModel>?,
    patches: List<PinModelPatch>?
): List<PinModel> {
    if (patches == null) {
        return targetEntities ?: emptyList()
    }

    if (targetEntities == null) {
        return patches.map { it.toPinModel() }
    }

    val list = LinkedList<PinModel>()
    val targetEntitiesById = LinkedHashMap<String, PinModel>()

    targetEntities.forEach {
        targetEntitiesById[it.id] = it
    }

    val newPins = LinkedList<PinModel>()

    for (patch in patches) {
        val patchId = patch.id
        val target = targetEntitiesById[patchId]
        if (target == null) {
            newPins.add(patch.toPinModel())
            continue
        }
        if (patch.remove) {
            targetEntitiesById.remove(patchId)
            continue
        }
        patchFields(target, patch)
    }

    list.addAll(targetEntitiesById.values)
    list.addAll(newPins)

    return list
}

private fun PinModelPatch.toPinModel(): PinModel {
    val pinModel = PinModel(
        id = id,
        type = type!!
    )
    patchFields(pinModel, this)
    return pinModel
}

fun patchAction(action: Action, patch: ActionPatch): Action {
    patchFields(action, patch)

    return action
}

fun patchCategory(category: Category, patch: CategoryPatch): Category {
    patchFields(category, patch)

    return category
}

fun patchPinType(pinType: PinType, pinTypePatch: PinTypePatch): PinType {
    return PinType(
        id = pinType.id,
        color = ColorUtil.parseColor(pinTypePatch.color)
    )
}

fun patchNodeType(nodeType: NodeType, nodeTypePatch: NodeTypePatch): NodeType {
    return NodeType(
        id = nodeType.id,
        color = ColorUtil.parseColor(nodeTypePatch.color)
    )
}

private inline fun <reified T : Any, reified P : Patch> patchFields(objectToPatch: T, patch: P) {
    objectToPatch::class.java.declaredFields.forEach {
        val field = try {
            patch::class.java.getDeclaredField(it.name)
        } catch (e: Exception) {
            return@forEach
        }

        useFields(it, field) {
            field.getAnnotation(Patchable::class.java) ?: return@useFields
            val patchedValue = field.get(patch) ?: return@useFields
            it.set(objectToPatch, patchedValue)
        }
    }
}

private fun useFields(vararg fields: Field, block: () -> Unit) {
    fields.forEach { it.isAccessible = true }
    block()
    fields.forEach { it.isAccessible = false }
}

fun main() {
    val category = Category(
        path = "event.bebra",
        name = "bebeaebabee"
    )

    val categoryPatch = CategoryPatch(
        path = "event.bebra",
        name = "testss"
    )

    patchFields(category, categoryPatch)
    println(category.name)

    val action = Action(
        id = "fafsd",
        name = "sdfaf",
        input = emptySet(),
        output = emptySet(),
        iconPath = "fasdf",
        category = "adfaas"
    )

    val actionPatch = ActionPatch(
        id = "dfasdf",
        name = "dsfasdfasd"
    )

    val prevValue = action.name
    patchFields(action, actionPatch)

    println("CHANGED = ${prevValue != action.name}")
}