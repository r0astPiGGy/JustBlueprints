package com.rodev.jbpkmp.presentation.screens.welcome_screen.components

import androidx.compose.runtime.Composable
import javax.swing.JFileChooser
import javax.swing.SwingUtilities
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun FileDialog(
    title: String,
    type: Int,
    selectionMode: Int = JFileChooser.FILES_ONLY,
    onCloseRequest: (path: String?) -> Unit
) = SwingUtilities.invokeLater {
    JFileChooser().apply {
        dialogTitle = title
        dialogType = type
        fileSelectionMode = selectionMode

        if (selectionMode == JFileChooser.FILES_ONLY) {
            fileFilter = FileNameExtensionFilter("JSON", "json")
        }

        val result = if (dialogType == JFileChooser.OPEN_DIALOG)
            showOpenDialog(null)
        else
            showSaveDialog(null)

        if (result == JFileChooser.APPROVE_OPTION)
            onCloseRequest(selectedFile.path)
        else
            onCloseRequest(null)
    }
}