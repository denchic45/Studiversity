package com.denchic45.kts.data.storage

import android.content.Context
import java.io.File
import java.io.FileOutputStream

class SubmissionAttachmentLocalStorage(context: Context) {

    private val internalDir = context.applicationContext.filesDir
    private val submissionLocalPath = File("${internalDir.path}/submissions")

    fun getByContentIdAndStudentId(contentId: String, studentId: String): List<File> {
        val attachmentPath =  getSubmissionLocalPath(contentId, studentId)
        val itContentDir = File(attachmentPath)
        return itContentDir.listFiles()!!.asList()
    }

    private fun getSubmissionLocalPath(contentId: String, studentId: String): String {
        return submissionLocalPath.path + '/' + contentId + '/' + studentId
    }

    private fun getSubmissionsLocalPath(contentId: String): String {
        return submissionLocalPath.path + '/' + contentId
    }

    fun save(contentId: String, studentId: String, name:String, bytes:ByteArray): File {
        val attachmentPath =  getSubmissionLocalPath(contentId, studentId)
        File(attachmentPath).mkdirs()
        val contentDir = File(attachmentPath, name)
        contentDir.createNewFile()
        val fileOutputStream = FileOutputStream(contentDir)
        fileOutputStream.write(bytes)
        fileOutputStream.close()
        return contentDir
    }
}