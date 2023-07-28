package com.rodev.jmcc_extractor.loader

interface DataLoader<T> {

    fun load(): T

}
