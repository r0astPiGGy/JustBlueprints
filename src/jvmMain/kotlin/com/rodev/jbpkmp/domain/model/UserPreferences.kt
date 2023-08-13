package com.rodev.jbpkmp.domain.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.io.File

//class UserPreferences private constructor(
//    data: Data
//) {
//
//    private val values = hashMapOf<String, Any>()
//
//    init {
//        loadValues(data)
//        addDefaults()
//    }
//
//    private fun loadValues(data: Data) {
//        data.values.forEach(::loadValue)
//    }
//
//    private fun loadValue(id: String, primitive: JsonPrimitive) {
//        values[id] = primitive.booleanOrNull ?: primitive.intOrNull ?: primitive.contentOrNull ?: return
//    }
//
//    private fun addDefaults() {
//        addIfNull(USE_DARK_THEME, true)
//        addIfNull(OPEN_LAST_PROJECT, false)
//        addIfNull(LANGUAGE_CODE, "ru")
//    }
//
//    private fun addIfNull(id: String, value: Any) {
//        values.putIfAbsent(id, value)
//    }
//
//    fun getBoolean(id: String): Boolean {
//
//    }
//
//    fun getString(id: String): String {
//
//    }
//
//    fun getInteger(id: String): Int {
//
//    }
//
//    fun setBoolean(id: String, boolean: Boolean) {
//
//    }
//
//    fun setInteger(id: String, int: Int) {
//
//    }
//
//    fun setString(id: String, string: String) {
//
//    }
//
//    fun saveTo(file: File, json: Json = Json) {
//
//    }
//
//    companion object {
//
//        fun loadFrom(file: File): UserPreferences {
//
//        }
//
//    }
//
//    @Serializable
//    private data class Data(
//        val values: Map<String, JsonPrimitive>
//    )
//}
//
//private const val USE_DARK_THEME = "useDarkTheme"
//private const val OPEN_LAST_PROJECT = "openLastProject"
//private const val LANGUAGE_CODE = "languageCode"