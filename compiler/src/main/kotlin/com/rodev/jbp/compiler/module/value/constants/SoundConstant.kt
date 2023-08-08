package com.rodev.jbp.compiler.module.value.constants

import com.rodev.jbp.compiler.json.inheritBuilder
import com.rodev.jbp.compiler.module.value.CValue
import com.rodev.jbp.compiler.module.value.ValueType
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.put

class SoundConstant(
    val sound: String,
    val volume: Number,
    val pitch: Number
) : CValue(ValueType.Sound) {

    override fun toJson(): JsonObject {
        return super.toJson().inheritBuilder {
            put("sound", sound)
            put("volume", volume)
            put("pitch", pitch)
        }
    }

}