package com.rodev.jbp.compiler.module.value

import com.rodev.jbp.compiler.json.inheritBuilder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.put

class VariableConstant(
    val name: String,
    val scope: Scope
) : CValue(ValueType.Variable) {

    enum class Scope(val id: String) {
        LOCAL("local"),
        GAME("game"),
        SAVE("save")
    }

    override fun toJson(): JsonObject {
        return super.toJson().inheritBuilder {
            put("variable", name)
            put("scope", scope.id)
        }
    }

}