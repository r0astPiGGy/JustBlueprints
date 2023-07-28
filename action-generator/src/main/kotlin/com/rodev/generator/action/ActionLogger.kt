package com.rodev.generator.action

import java.io.File

object ActionLogger {

    private val messages = mutableListOf<String>()

    fun log(msg: String) {
        messages += msg
    }

    fun writeTo(file: File) {
        file.writeText(messages.joinToString(separator = "\n") { it })
    }

}