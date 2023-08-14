package com.rodev.jbpkmp.data

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import com.rodev.jbpkmp.domain.repository.ActionDataSource
import com.rodev.jbpkmp.domain.repository.IconDataSource

class IconDataSourceImpl(
    actionDataSource: ActionDataSource
) : IconDataSource {

    private val icons = mutableMapOf<String, ImageBitmap>()

    init {
        actionDataSource.getAllActions().forEach {
            loadIconByPath(it.iconPath)
        }
    }

    private fun loadIconByPath(path: String) {
        try {
            icons[path] = useResource(path, ::loadImageBitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getIconById(id: String): ImageBitmap? {
        return icons[id]
    }
}