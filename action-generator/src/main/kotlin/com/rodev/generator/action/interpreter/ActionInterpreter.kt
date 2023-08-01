package com.rodev.generator.action.interpreter

import com.rodev.generator.action.entity.NodeCompound

interface ActionInterpreter<T> {

    fun interpret(list: List<T>): List<NodeCompound>

    fun List<T>.interpret(mapFunction: (T) -> NodeCompound): List<NodeCompound> {
        return map(mapFunction)
    }

}