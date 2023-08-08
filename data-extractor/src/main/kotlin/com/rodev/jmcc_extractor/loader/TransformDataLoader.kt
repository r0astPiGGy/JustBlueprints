package com.rodev.jmcc_extractor.loader


private class TransformDataLoader<T, K>(
    private val dataLoader: DataLoader<T>,
    private val transformFunction: TransformFunction<T, K>
): DataLoader<K> {
    override fun load(): K {
        val data = dataLoader.load()

        return transformFunction(data)
    }
}

typealias TransformFunction<T, K> = (T) -> K

fun <T, K> DataLoader<T>.transform(transformFunction: TransformFunction<T, K>): DataLoader<K> {
    return TransformDataLoader(
        dataLoader = this,
        transformFunction = transformFunction
    )
}
