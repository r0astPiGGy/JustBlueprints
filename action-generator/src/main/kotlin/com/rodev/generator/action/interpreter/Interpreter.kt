package com.rodev.generator.action.interpreter

interface Interpreter<I, O> {

    fun interpret(input: I): O

}