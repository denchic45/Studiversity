package com.denchic45.studiversity.util

import okio.Path
import java.io.File
import java.net.FileNameMap
import java.net.URLConnection


fun Path.getType() = getMimeType().split("/")[0]

fun Path.getMimeType(): String {
    val fileNameMap: FileNameMap = URLConnection.getFileNameMap()
    val mime = fileNameMap.getContentTypeFor(this.name) ?: return ""
    return if (mime.split("/")[0] == "application") {
        return if (mime.contains(".")) {
            mime.substring(mime.lastIndexOf(".") + 1)
        } else {
            mime.substring(mime.indexOf('/') + 1)
        }
    } else mime.split("/")[0]
}

fun File.clearAndDelete(): Boolean {
    val allContents = this.listFiles()
    if (allContents != null) {
        for (file in allContents) {
            file.clearAndDelete()
        }
    }
    return this.delete()

}


fun Path.getExtension(): String {
    val fileName = name
    val i: Int = fileName.lastIndexOf('.')
    return fileName.substring(i + 1)
}

object Files {
    fun nameWithoutTimestamp(name: String): String {
        return name.substring(name.indexOf("_") + 1)
    }
}