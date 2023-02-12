package com.denchic45.kts.data.storage.local

import com.denchic45.kts.util.clearAndDelete
import java.io.File
import java.io.FileOutputStream
import com.denchic45.kts.util.SystemDirs
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class SubmissionAttachmentLocalStorage @Inject constructor(systemDirs: SystemDirs) {

    private val internalDir = systemDirs.appDirectory
    private val submissionDir = File("${internalDir.path}/submissions")

    fun getByContentIdAndStudentId(contentId: String, studentId: String): List<File> {
        val attachmentPath =  getSubmissionLocalPath(contentId, studentId)
        val itContentDir = File(attachmentPath)
        return itContentDir.listFiles()?.asList() ?: emptyList()
    }

    private fun getSubmissionLocalPath(contentId: String, studentId: String): String {
        return submissionDir.path + '/' + contentId + '/' + studentId
    }

    private fun getSubmissionsLocalPath(contentId: String): String {
        return submissionDir.path + '/' + contentId
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

    fun deleteByContentId(contentId: String) {
        val contentDir = File(getSubmissionsLocalPath(contentId))
        contentDir.clearAndDelete()
    }
}