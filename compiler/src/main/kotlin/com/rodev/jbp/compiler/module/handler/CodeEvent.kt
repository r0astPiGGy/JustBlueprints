package com.rodev.jbp.compiler.module.handler

import com.rodev.jbp.compiler.json.inheritBuilder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.put

class CodeEvent(
    val event: String
) : CodeHandler(type = "event") {

    override fun toJson(): JsonObject {
        return super.toJson().inheritBuilder {
            put("event", event)
        }
    }

}