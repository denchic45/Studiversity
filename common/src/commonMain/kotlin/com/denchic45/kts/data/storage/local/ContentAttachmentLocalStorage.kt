package com.denchic45.kts.data.storage.local

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


actual class ContentAttachmentLocalStorage @Inject constructor(
    context: Context,
) {

    private val internalDir = context.applicationContext.filesDir
    private val contentPath = File("${internalDir.path}/contents")

    actual fun deleteFromLocal(contentId: String) {
        val contentDir = File(contentPath.path + '/' + getAttachmentPath(contentId) + contentId)
        contentDir.listFiles()?.forEach { it.delete() }
        contentDir.delete()
    }

    actual fun get(contentId: String, url: String): File? {
        val timestamp = url.substring(url.lastIndexOf("/o/"), url.indexOf('_'))
        val attachmentPath = getAttachmentPath(contentId)
        val itContentDir = File(attachmentPath)
        return itContentDir.listFiles { _, name -> name.startsWith(timestamp) }?.firstOrNull()
    }

    private fun getAttachmentPath(contentId: String): String {
        return contentPath.path + '/' + contentId
    }

    actual fun saveFile(contentId: String, name: String, bytes: ByteArray): File {
        val attachmentPath = getAttachmentPath(contentId)
        File(attachmentPath).mkdirs()
        val contentDir = File(attachmentPath, name)
        contentDir.createNewFile()
        val fileOutputStream = FileOutputStream(contentDir)
        fileOutputStream.write(bytes)
        fileOutputStream.close()
        return contentDir
    }
}