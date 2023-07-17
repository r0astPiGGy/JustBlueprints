package com.rodev.jbpkmp.presentation.screens.welcome_screen.components

import androidx.compose.runtime.Composable
import javax.swing.JFileChooser
import javax.swing.SwingUtilities
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun FileDialog(
    title: String,
    selectionMode: Int = JFileChooser.FILES_ONLY,
    type: Int,
    onCloseRequest: (path: String?) -> Unit
) = SwingUtilities.invokeLater {
    JFileChooser().apply {
        dialogTitle = title
        dialogType = type
        fileSelectionMode = selectionMode

        if (fileSelectionMode == JFileChooser.FILES_ONLY)
            fileFilter = FileNameExtensionFilter("JSON", "json")

        val code = showSaveDialog(null)
        if (code == JFileChooser.APPROVE_OPTION)
            onCloseRequest(selectedFile.path)
        else
            onCloseRequest(null)
    }
}