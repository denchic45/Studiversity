package com.denchic45.studiversity.ui.filepicker

import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileSystemView


fun chooseFile(
    title: String,
    onResult: (result: File) -> Unit
) {
    val fileChooser = initFileChooser(title)

    if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        val file = fileChooser.selectedFile
        println("choose file or folder is: $file")
        onResult(file)
    }
}


fun chooseMultipleFiles(
    title: String,
    onResult: (result: Array<File>) -> Unit
) {

    val fileChooser = initFileChooser(title)

    if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        val files = fileChooser.selectedFiles
        println("choose file or folder is: $files")
        onResult(files)
    }

}

private fun initFileChooser(title: String): JFileChooser {
    return JFileChooser(FileSystemView.getFileSystemView()).apply {
        currentDirectory = File(System.getProperty("user.dir"))
        dialogTitle = title
        fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
        isAcceptAllFileFilterUsed = true
        selectedFile = null
        currentDirectory = null
    }
}


