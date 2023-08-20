package com.rodev.jbpkmp.domain.compiler

import com.rodev.jbpkmp.domain.compiler.exception.BlueprintCompileException
import com.rodev.jbpkmp.domain.model.Blueprint
import com.rodev.jbpkmp.domain.source.NodeDataSource
import kotlin.jvm.Throws

class BlueprintCompiler(
    private val nodeDataSource: NodeDataSource
) {

    @Throws(BlueprintCompileException::class)
    suspend fun compile(blueprint: Blueprint): String {
        return BlueprintCompilerHelper(nodeDataSource, blueprint).compile()
    }

}