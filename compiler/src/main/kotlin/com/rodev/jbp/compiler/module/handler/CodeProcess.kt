package com.rodev.jbp.compiler.module.handler

import com.rodev.jbp.compiler.json.inheritBuilder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.put

class CodeProcess(
    val name: String,
    var isHidden: Boolean = false
) : CodeHandler(type = "process") {

    override fun toJson(): JsonObject {
        return super.toJson().inheritBuilder {
            put("is_hidden", isHidden)
            put("name", name)
        }
    }

}