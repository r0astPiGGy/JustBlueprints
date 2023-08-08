package com.rodev.jbp.compiler.module.value

import kotlinx.serialization.json.JsonObject

object EmptyValue : CValue(ValueType.Empty) {

    override fun toJson(): JsonObject {
        return JsonObject(emptyMap())
    }

}