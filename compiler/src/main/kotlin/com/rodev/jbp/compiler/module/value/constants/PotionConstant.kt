package com.rodev.jbp.compiler.module.value.constants

import com.rodev.jbp.compiler.json.inheritBuilder
import com.rodev.jbp.compiler.module.value.CValue
import com.rodev.jbp.compiler.module.value.ValueType
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.put

class PotionConstant(
    val potion: String,
    val amplifier: Number,
    val duration: Number
) : CValue(ValueType.Potion) {

    override fun toJson(): JsonObject {
        return super.toJson().inheritBuilder {
            put("potion", potion)
            put("amplifier", amplifier)
            put("duration", duration)
        }
    }

}