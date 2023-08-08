package com.rodev.jbp.compiler.module.value.constants

import com.rodev.jbp.compiler.json.inheritBuilder
import com.rodev.jbp.compiler.json.put
import com.rodev.jbp.compiler.module.value.CValue
import com.rodev.jbp.compiler.module.value.Value
import com.rodev.jbp.compiler.module.value.ValueType
import kotlinx.serialization.json.JsonObject

class ArrayConstant(
    val values: List<Value>
) : CValue(ValueType.Array) {

    override fun toJson(): JsonObject {
        return super.toJson().inheritBuilder {
            put("values", values.map(Value::toJson))
        }
    }

}