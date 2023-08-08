package com.rodev.jbp.compiler.module.value.constants

import com.rodev.jbp.compiler.json.inheritBuilder
import com.rodev.jbp.compiler.json.put
import com.rodev.jbp.compiler.module.value.CValue
import com.rodev.jbp.compiler.module.value.Value
import com.rodev.jbp.compiler.module.value.ValueType
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

class MapConstant(
    private val values: Map<Value, Value>
) : CValue(ValueType.Map) {

    override fun toJson(): JsonObject {
        return super.toJson().inheritBuilder {
            put("values", values.map { it.toJson() })
        }
    }

    fun Map.Entry<Value, Value>.toJson(): JsonObject = buildJsonObject {
        put("key", key.toJson())
        put("value", value.toJson())
    }

}