package com.rodev.jbpkmp.domain.source

import androidx.compose.ui.graphics.ImageBitmap

interface IconDataSource {

    fun getIconById(id: String): ImageBitmap?

}