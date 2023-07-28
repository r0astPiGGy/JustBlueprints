package com.rodev.generator.action.interpreter

abstract class ListInterpreter<I, O> : Interpreter<List<I>, List<O>> {
    override fun interpret(input: List<I>): List<O> {
        return input.mapNotNull(::interpretElement)
    }

    protected abstract fun interpretElement(input: I): O?
}