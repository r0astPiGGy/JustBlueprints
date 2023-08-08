package com.rodev.jbp.compiler.module.value.constants

import com.rodev.jbp.compiler.json.inheritBuilder
import com.rodev.jbp.compiler.module.value.CValue
import com.rodev.jbp.compiler.module.value.ValueType
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.put

open class EnumConstant(
    val value: String
) : CValue(ValueType.Enum) {

    override fun toJson(): JsonObject {
        return super.toJson().inheritBuilder {
            put("enum", value)
        }
    }

}

class BooleanConstant(value: Boolean) : EnumConstant(
    if (value) {
        "TRUE"
    } else {
        "FALSE"
    }
)