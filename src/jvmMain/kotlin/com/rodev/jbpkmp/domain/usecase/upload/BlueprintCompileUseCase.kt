package com.rodev.jbpkmp.domain.usecase.upload

import com.rodev.jbpkmp.domain.compiler.BlueprintCompiler
import com.rodev.jbpkmp.domain.model.Blueprint

class BlueprintCompileUseCase(
    private val compiler: BlueprintCompiler
) {

    suspend operator fun invoke(blueprint: Blueprint): String {
        return compiler.compile(blueprint)
    }

}