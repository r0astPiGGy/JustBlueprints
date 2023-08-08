package com.rodev.jbp.compiler.module

import kotlinx.serialization.json.JsonObject

abstract class CodeBlock {

    abstract val length: Int

    abstract fun toJson(): JsonObject

}