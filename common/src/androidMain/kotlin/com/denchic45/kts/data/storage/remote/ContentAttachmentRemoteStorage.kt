package com.denchic45.kts.data.storage

import android.net.Uri
import com.denchic45.kts.data.DownloadByUrlApi
import com.denchic45.kts.domain.model.Attachment
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ContentAttachmentRemoteStorage(
    private val firebaseStorage: FirebaseStorage,
    private val retrofit: Retrofit,
) {

    private val contentAttachmentsRef = firebaseStorage.reference.child("content_attachments")

    suspend fun get(url: String): Pair<String, ByteArray> {
        val response = retrofit.create(DownloadByUrlApi::class.java)
            .invoke(url)

        val name = firebaseStorage.getReferenceFromUrl(url).name
        return name to response.body()!!.bytes()
    }

    suspend fun deleteByContentId(contentId: String) {
        contentAttachmentsRef.child(contentId).listAll().await()
            .items.forEach { it.delete().await() }
    }

    suspend fun addContentAttachments(
        parentId: String,
        attachments: List<Attachment>,
    ): List<String> {
        return attachments.map { attachment ->
            val timestamp =
                DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss").format(LocalDateTime.now())
            val filePath = contentAttachmentsRef.child("$parentId/${timestamp}_${attachment.name}")
            filePath.putFile(Uri.fromFile(attachment.file)).await()
            filePath.downloadUrl.await().toString()
        }
    }

    suspend fun update(id: String, attachments: List<Attachment>): List<String> {
        val currentFiles = contentAttachmentsRef.child(id).listAll().await().items
        val currentFileNames = currentFiles.map { it.name }.toSet()
        val updatedFileNames = attachments.map { it.name }.toSet()

        val added: List<Attachment> = attachments.filterNot { currentFileNames.contains(it.name) }
        val removed: List<StorageReference> =
            currentFiles.filterNot { updatedFileNames.contains(it.name) }

        removed.forEach { it.delete().await() }
        val uploaded = addContentAttachments(id, added)

        return currentFiles.minus(removed.toSet())
            .map { it.downloadUrl.await().toString() } + uploaded
    }
}