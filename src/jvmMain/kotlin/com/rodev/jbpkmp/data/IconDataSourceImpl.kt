package com.rodev.jbpkmp.data

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import com.rodev.jbpkmp.domain.source.ActionDataSource
import com.rodev.jbpkmp.domain.source.IconDataSource

class IconDataSourceImpl(
    actionDataSource: ActionDataSource
) : IconDataSource {

    private val icons = mutableMapOf<String, ImageBitmap>()

    init {
        val absentIcons = mutableSetOf<String>()
        actionDataSource.getAllActions().forEach {
            val result = loadIconByPath(it.iconPath)

            if (!result) {
                absentIcons.add(it.iconPath)
            }
        }

        if (absentIcons.isNotEmpty()) {
            System.err.println("Icons not found: [${absentIcons.size}]:")
            absentIcons.forEach {
                System.err.println("- $it")
            }
        }
    }

    private fun loadIconByPath(path: String): Boolean {
        return try {
            icons[path] = useResource(path, ::loadImageBitmap)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun getIconById(id: String): ImageBitmap? {
        return icons[id]
    }
}