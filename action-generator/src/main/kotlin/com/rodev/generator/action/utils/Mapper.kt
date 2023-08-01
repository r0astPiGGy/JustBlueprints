package com.rodev.generator.action.utils

fun <K, V> Iterable<V>.toMap(keyExtractor: (V) -> K): Map<K, V> {
    return hashMapOf<K, V>().also { map ->
        forEach {
            map[keyExtractor(it)] = it
        }
    }
}