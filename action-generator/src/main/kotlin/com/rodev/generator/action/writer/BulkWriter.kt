package com.rodev.generator.action.writer

import java.io.File

typealias WriteFunction = File.() -> Unit

interface BulkWriterScope : FolderScope {

    fun folder(path: String, folderScope: FolderScope.() -> Unit = {})

}

interface FolderScope {

    fun file(name: String, writeFunction: WriteFunction)

}

class BulkWriter private constructor(): FolderScopeImpl(), BulkWriterScope {

    private val folders = mutableListOf<Folder>()

    override fun folder(path: String, folderScope: FolderScope.() -> Unit) {
        with(Folder(path)) {
            folderScope()
            folders += this
        }
    }

    fun write() {
        folders.forEach { folder ->
            folder.write()
            files.forEach {
                it.writeInFolder(folder.folder)
            }
        }
    }

    companion object {

        fun bulkWrite(scope: BulkWriterScope.() -> Unit) {
            with(BulkWriter()) {
                scope()
                write()
            }
        }

    }
}

data class FileWrapper(
    val name: String,
    val writeFunction: WriteFunction
)

internal fun FileWrapper.writeInFolder(folder: File) {
    writeFunction(File(folder, name))
}

open class FolderScopeImpl : FolderScope {

    protected val files = mutableListOf<FileWrapper>()

    override fun file(name: String, writeFunction: WriteFunction) {
        files += FileWrapper(name = name, writeFunction = writeFunction)
    }
}

internal class Folder(
    path: String
) : FolderScopeImpl() {

    val folder = File(path)

    fun write() {
        folder.mkdirs()
        files.forEach {
            it.writeInFolder(folder)
        }
    }
}
