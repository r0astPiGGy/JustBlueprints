package com.rodev.jbpkmp.domain.compiler.exception

import com.rodev.jbp.compiler.module.value.ValueType

sealed class BlueprintCompileException(
    val nodeId: String,
    val pinId: String,
    message: String
) : Exception(message) {

    class WrongArgument(
        val expected: ValueType,
        val actual: ValueType?,
        nodeId: String,
        pinId: String
    ) : BlueprintCompileException(nodeId, pinId, "Expected argument typeof '$expected', but got '$actual'")

    class NotConnected(
        nodeId: String,
        pinId: String
    ) : BlueprintCompileException(nodeId, pinId, "Pin is not connected")

}