package com.denchic45.kts.data.storage.remote

import android.net.Uri
import com.denchic45.kts.data.domain.model.Attachment
import com.denchic45.kts.data.network.DownloadByUrlApi
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

actual class SubmissionAttachmentRemoteStorage @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
//    private val retrofit: Retrofit,
    private val client: HttpClient
) {
    private val submissionAttachmentsRef = firebaseStorage.reference.child("submission_attachments")


    private fun getSubmissionReference(contentId: String, studentId: String): StorageReference {
        return submissionAttachmentsRef.child(contentId).child(studentId)
    }

    private fun getSubmissionsReference(contentId: String): StorageReference {
        return submissionAttachmentsRef.child(contentId)
    }

    actual suspend fun addSubmissionAttachments(
        contentId: String,
        studentId: String,
        attachments: List<Attachment>,
    ): List<String> {
        return attachments.map { attachment ->
            try {
                getSubmissionReference(
                    contentId,
                    studentId
                ).child(attachment.name).downloadUrl.await().toString()
            } catch (exception: Exception) {
                if (exception is StorageException &&
                    exception.errorCode == StorageException.ERROR_OBJECT_NOT_FOUND
                ) {
                    val timestamp = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss")
                        .format(LocalDateTime.now())
                    val filePath =
                        submissionAttachmentsRef.child("$contentId/$studentId/${timestamp}_${attachment.name}")
                    filePath.putFile(Uri.fromFile(attachment.file)).await()
                    filePath.downloadUrl.await().toString()
                } else {
                    exception.printStackTrace()
                    throw exception
                }
            }
        }
    }

    actual fun getAttachmentName(url: String): String {
        return firebaseStorage.getReferenceFromUrl(url).name
    }

    actual suspend fun getAttachmentBytes(url: String): ByteArray {
        return client.get { url(url) }.readBytes()
//        return retrofit.create(DownloadByUrlApi::class.java).invoke(url).body()!!.bytes()
    }

    actual suspend fun update(
        contentId: String,
        studentId: String,
        attachments: List<Attachment>,
    ): List<String> {
        val currentFiles = getSubmissionReference(contentId, studentId).listAll().await().items

        val currentFileNames = currentFiles.map { it.name }.toSet()
        val updatedFileNames = attachments.map { it.name }.toSet()

        val added = attachments.filterNot { currentFileNames.contains(it.name) }
        val removed = currentFiles.filterNot { updatedFileNames.contains(it.name) }

        removed.forEach { it.delete().await() }
        val uploaded = addSubmissionAttachments(contentId, studentId, added)

        return currentFiles.minus(removed.toSet())
            .map { it.downloadUrl.await().toString() } + uploaded
    }

    actual suspend fun deleteFilesByContentId(contentId: String) {
        getSubmissionsReference(contentId).deleteFolder()
    }

    suspend fun StorageReference.deleteFolder() {
        listAll().await().apply {
            items.forEach {
                it.delete().await()
            }
            prefixes.forEach { it.deleteFolder() }
        }
    }
}