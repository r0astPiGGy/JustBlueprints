package com.rodev.jbpkmp.domain.model

class CodeLoadCommand(
    private val link: String
) {

    fun toString(force: Boolean): String {
        val forceArg = if (force) " force" else ""

        return "/module loadUrl$forceArg $link"
    }

}