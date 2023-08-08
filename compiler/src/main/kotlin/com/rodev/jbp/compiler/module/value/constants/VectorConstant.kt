package com.rodev.jbp.compiler.module.value.constants

import com.rodev.jbp.compiler.json.inheritBuilder
import com.rodev.jbp.compiler.module.value.CValue
import com.rodev.jbp.compiler.module.value.ValueType
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.put

class VectorConstant(
    val x: Number,
    val y: Number,
    val z: Number
) : CValue(ValueType.Vector) {

    override fun toJson(): JsonObject {
        return super.toJson().inheritBuilder {
            put("x", x)
            put("y", y)
            put("z", z)
        }
    }

}