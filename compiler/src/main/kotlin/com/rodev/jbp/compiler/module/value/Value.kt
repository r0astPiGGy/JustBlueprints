package com.rodev.jbp.compiler.module.value

import kotlinx.serialization.json.JsonObject

abstract class Value {

    abstract val type: ValueType

    abstract fun toJson(): JsonObject

}