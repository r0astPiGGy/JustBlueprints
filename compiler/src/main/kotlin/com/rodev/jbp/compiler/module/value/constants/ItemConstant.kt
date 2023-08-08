package com.rodev.jbp.compiler.module.value.constants

import com.rodev.jbp.compiler.json.inheritBuilder
import com.rodev.jbp.compiler.module.value.CValue
import com.rodev.jbp.compiler.module.value.ValueType
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.put

class ItemConstant(val item: String) : CValue(ValueType.Item) {

    override fun toJson(): JsonObject {
        return super.toJson().inheritBuilder {
            put("item", item)
        }
    }

}