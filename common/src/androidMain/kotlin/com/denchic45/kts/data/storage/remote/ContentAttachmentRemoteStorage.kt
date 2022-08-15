package com.denchic45.kts.data.storage.remote

import com.denchic45.kts.data.domain.model.Attachment
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

actual class ContentAttachmentRemoteStorage @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val client: HttpClient,
) {

    private val contentAttachmentsRef = firebaseStorage.reference.child("content_attachments")

    actual suspend fun get(url: String): ByteArray = client.get(url).readBytes()

    actual fun getAttachmentName(url: String): String {
        return firebaseStorage.getReferenceFromUrl(url).name
    }

    actual suspend fun deleteByContentId(contentId: String) {
        contentAttachmentsRef.child(contentId).listAll().await()
            .items.forEach { it.delete().await() }
    }

    actual suspend fun addContentAttachments(
        parentId: String,
        attachments: List<Attachment>,
    ): List<String> {
        return attachments.map { attachment ->
            val timestamp =
                DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss").format(LocalDateTime.now())
            val filePath = contentAttachmentsRef.child("$parentId/${timestamp}_${attachment.name}")
            filePath.putBytes(attachment.file.readBytes()).await()
            filePath.downloadUrl.await().toString()
        }
    }

    actual suspend fun update(id: String, attachments: List<Attachment>): List<String> {
        val currentFiles = contentAttachmentsRef.child(id).listAll().await().items
        val currentFileNames = currentFiles.map { reference -> reference.name }.toSet()
        val updatedFileNames = attachments.map { attachment -> attachment.name }.toSet()

        val added: List<Attachment> = attachments.filterNot { currentFileNames.contains(it.name) }
        val removed: List<StorageReference> =
            currentFiles.filterNot { updatedFileNames.contains(it.name) }

        removed.forEach { it.delete().await() }

        val uploaded = addContentAttachments(id, added)

        return currentFiles.minus(removed.toSet())
            .map { it.downloadUrl.await().toString() } + uploaded
    }
}