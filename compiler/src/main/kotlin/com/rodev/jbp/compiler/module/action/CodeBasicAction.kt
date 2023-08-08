package com.rodev.jbp.compiler.module.action

class CodeBasicAction(
    id: String,
    args: CodeActionArguments = emptyMap(),
    selection: String? = null,
    conditional: CodeAction?
) : CodeAction(id, args, selection, conditional)