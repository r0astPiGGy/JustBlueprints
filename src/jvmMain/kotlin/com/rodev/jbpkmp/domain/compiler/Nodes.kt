package com.rodev.jbpkmp.domain.compiler

import com.rodev.jbp.compiler.module.value.ValueType

object Nodes {

    object Type {

        const val BRANCH = "native_branch"
        
    }
    
    enum class Factory(val id: String, val valueType: ValueType) {
        ITEM("native_item_factory", ValueType.Item),
        LOCATION("native_location_factory", ValueType.Location),
        TEXT("native_text_factory", ValueType.Text),
        NUMBER("native_number_factory", ValueType.Number),
        SOUND("native_sound_factory", ValueType.Sound),
        ARRAY("native_array_factory", ValueType.Array)

        ;

        companion object {

            fun ValueType.toFactory(): Factory? {
                return values().find { it.valueType == this }
            }

        }
    }
}