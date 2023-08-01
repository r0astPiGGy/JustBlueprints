package com.rodev.generator.action.utils

object ColorUtil {

    fun parseColor(color: String): Int {
        val split = color.split(",")

        fun String.parseInt(): Int {
            return this.trim().toInt()
        }

        val r = split[0].parseInt()
        val g = split[1].parseInt()
        val b = split[2].parseInt()
        val a = 255

        return a and 0xFF shl 24 or
                (r and 0xFF shl 16) or
                (g and 0xFF shl 8) or (b and 0xFF shl 0)
    }

}