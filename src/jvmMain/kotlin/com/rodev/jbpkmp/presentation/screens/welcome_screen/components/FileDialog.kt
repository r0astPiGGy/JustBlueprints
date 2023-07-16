package com.rodev.jbpkmp.presentation.screens.welcome_screen.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.AwtWindow
import com.rodev.jbpkmp.presentation.ResString
import java.awt.FileDialog
import java.awt.Frame

@Composable
fun FileDialog(
    parent: Frame? = null,
    fileName: String = "*",
    onCloseRequest: (file: String?, directory: String?) -> Unit,
    openParam: Int
) = AwtWindow(
    create = {
        object : FileDialog(parent, ResString.chooseFile, openParam) {
            override fun setVisible(value: Boolean) {
                super.setVisible(value)

                if (value) onCloseRequest(file, directory) // TODO: Fix
            }
        }.apply {
            // Windows
            file = "$fileName.json"

            // Linux/macOS
            setFilenameFilter { _, name -> name.endsWith("json") }
        }
    },
    dispose = FileDialog::dispose
)