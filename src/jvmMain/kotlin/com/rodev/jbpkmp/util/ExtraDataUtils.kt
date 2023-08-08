package com.rodev.jbpkmp.util

import com.rodev.generator.action.entity.extra_data.CompoundExtraData
import com.rodev.generator.action.entity.extra_data.ExtraData

inline fun <reified T : ExtraData> ExtraData?.castTo(): T {
    if (this is CompoundExtraData) {
        return getExtraDataTypeOf<T>()!!
    }

    this as T

    return this
}

inline fun <reified T : ExtraData> ExtraData?.contains(): Boolean {
    if (this is CompoundExtraData) {
        return containsExtraDataOfType<T>()
    }

    return this is T
}

inline fun <reified T : ExtraData> ExtraData?.castOrNull(): T? {
    if (this is CompoundExtraData) {
        return getExtraDataTypeOf<T>()
    }

    return this as? T
}