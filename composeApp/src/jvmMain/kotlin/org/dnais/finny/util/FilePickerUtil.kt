package org.dnais.finny.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

object FilePickerUtil {

    suspend fun pickCsvFile(): File? {
        return withContext(Dispatchers.Main) {
            val dialog = FileDialog(null as Frame?, "Select CSV File", FileDialog.LOAD)
            dialog.file = "*.csv"
            dialog.isVisible = true

            val directory = dialog.directory
            val filename = dialog.file

            if (directory != null && filename != null) {
                File(directory, filename)
            } else {
                null
            }
        }
    }
}
