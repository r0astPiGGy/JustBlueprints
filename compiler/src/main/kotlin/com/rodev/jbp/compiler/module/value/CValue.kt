package com.rodev.jbp.compiler.module.value

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

abstract class CValue(
    override val type: ValueType
) : Value() {

    override fun toJson(): JsonObject {
        return buildJsonObject {
            put("type", type.id)
        }
    }

}