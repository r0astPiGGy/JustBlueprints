package com.rodev.jbpkmp.domain.compiler

import com.rodev.jbp.compiler.module.value.Value
import com.rodev.nodeui.model.Node

interface ValueFactory {

    fun createValue(factory: Nodes.Factory, node: Node): Value

}