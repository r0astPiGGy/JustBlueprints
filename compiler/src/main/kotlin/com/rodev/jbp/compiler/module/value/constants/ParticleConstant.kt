package com.rodev.jbp.compiler.module.value.constants

import com.rodev.jbp.compiler.json.inheritBuilder
import com.rodev.jbp.compiler.json.putNotNull
import com.rodev.jbp.compiler.module.value.CValue
import com.rodev.jbp.compiler.module.value.ValueType
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.put

class ParticleConstant(
    val particle: String,
    val count: Number,
    val motion: Motion,
    val spread: Spread,
    val material: String?,
    val color: String?,
    val size: String?
) : CValue(ValueType.Particle) {

    data class Motion(
        val x: Number, val y: Number, val z: Number
    )

    data class Spread(
        val x: Number, val y: Number
    )

    override fun toJson(): JsonObject {
        return super.toJson().inheritBuilder {
            put("particle_type", particle)
            put("count", count)
            put("first_spread", spread.x)
            put("second_spread", spread.y)
            put("x_motion", motion.x)
            put("y_motion", motion.y)
            put("z_motion", motion.z)
            putNotNull("material", material)
            putNotNull("color", color)
            putNotNull("size", size)
        }
    }

}