package com.rodev.jbp.compiler.module

import com.rodev.jbp.compiler.module.action.CodeAction

interface CodeContainer {

    val actions: MutableList<CodeAction>

}