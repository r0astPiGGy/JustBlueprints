package com.rodev.jbpkmp.domain.compiler

import com.rodev.jbpkmp.domain.compiler.exception.BlueprintCompileException
import com.rodev.jbpkmp.domain.model.Blueprint
import kotlin.jvm.Throws

class BlueprintCompiler {

    @Throws(BlueprintCompileException::class)
    suspend fun compile(blueprint: Blueprint): String {
        return BlueprintCompilerHelper(blueprint).compile()
    }

}