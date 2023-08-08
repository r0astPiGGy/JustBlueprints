package com.rodev.jbp.compiler.module.handler

import com.rodev.jbp.compiler.json.inheritBuilder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.put

class CodeFunction(
    val name: String,
    var isHidden: Boolean = false
) : CodeHandler(type = "function") {

    override fun toJson(): JsonObject {
        return super.toJson().inheritBuilder {
            put("is_hidden", isHidden)
            put("name", name)
        }
    }

}