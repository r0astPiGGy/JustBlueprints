package com.rodev.jbpkmp.domain.repository

import androidx.compose.ui.graphics.ImageBitmap

interface IconDataSource {

    fun getIconById(id: String): ImageBitmap?

}