package com.denchic45.kts.data.storage.local

import com.denchic45.kts.util.SystemDirs
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class ContentAttachmentLocalStorage @Inject constructor(systemDirs: SystemDirs) {

    private val internalDir = systemDirs.appDir
    private val contentPDir = File("${internalDir.path}/contents")

    fun delete(contentId: String) {
        val contentDir =
            File(contentPDir.path + '/' + getAttachmentsPath(contentId) + contentId)
        contentDir.listFiles()?.forEach { it.delete() }
        contentDir.delete()
    }

    fun get(contentId: String, name: String): File? {
        val attachmentPath = getAttachmentPath(contentId, name)
        return File(attachmentPath).run {
            if (exists())
                this
            else null
        }
    }

    private fun getAttachmentsPath(contentId: String): String {
        return contentPDir.path + File.separator + contentId
    }

    private fun getAttachmentPath(contentId: String, name: String): String {
        return contentPDir.path + File.separator + contentId + File.separator + name
    }

    fun saveFile(contentId: String, name: String, bytes: ByteArray): File {
        val attachmentsPath = getAttachmentsPath(contentId)
        File(attachmentsPath).mkdirs()
        val contentDir = File(attachmentsPath, name)
        contentDir.createNewFile()
        val fileOutputStream = FileOutputStream(contentDir)
        fileOutputStream.write(bytes)
        fileOutputStream.close()
        return contentDir
    }

    fun getByContent(contentId: String): List<File> {
        val attachmentPath = getSubmissionPath(contentId)
        val itContentDir = File(attachmentPath)
        return itContentDir.listFiles()?.asList() ?: emptyList()
    }

    private fun getSubmissionPath(contentId: String): String {
        return contentPDir.path + '/' + contentId
    }
}