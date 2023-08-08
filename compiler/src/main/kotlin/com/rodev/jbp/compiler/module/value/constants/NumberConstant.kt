package com.rodev.jbp.compiler.module.value.constants

import com.rodev.jbp.compiler.json.inheritBuilder
import com.rodev.jbp.compiler.module.value.CValue
import com.rodev.jbp.compiler.module.value.ValueType
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.put

class NumberConstant(
    val number: Number
) : CValue(ValueType.Number) {

    override fun toJson(): JsonObject {
        return super.toJson().inheritBuilder {
            put("number", number)
        }
    }

}