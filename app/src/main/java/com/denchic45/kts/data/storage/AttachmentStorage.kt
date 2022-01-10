package com.denchic45.kts.data.storage

import android.content.Context
import android.net.Uri
import com.denchic45.kts.data.DownloadByUrlApi
import com.denchic45.kts.data.model.domain.Attachment
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class AttachmentStorage @Inject constructor(
    context: Context,
    private val firebaseStorage: FirebaseStorage,
    private val retrofit: Retrofit
) {

    private val contentAttachmentsRef = firebaseStorage.reference.child("content_attachments")
    private val internalDir = context.applicationContext.filesDir
    private val contentPath = File("${internalDir.path}/contents")

    suspend fun addContentAttachments(
        parentId: String,
        attachments: List<Attachment>
    ): List<String> {
        return attachments.map {
            val timestamp =
                DateTimeFormatter.ofPattern("DD-MM-yyyy-HH-mm-ss").format(LocalDateTime.now())
            val filePath = contentAttachmentsRef.child("$parentId/${timestamp}_${it.name}")
            filePath.putFile(Uri.fromFile(it.file)).await()
            filePath.downloadUrl.await().toString()
        }
    }

    suspend fun get(contentId: String, urls: List<String>): List<Attachment> {
        return if (urls.isEmpty())
            emptyList()
        else urls.map { url ->
            val timestamp = url.substring(url.lastIndexOf("/o/"), url.indexOf('_'))
            val itContentDir = File(contentPath.path + '/' + contentId)
            val listFiles = itContentDir.listFiles { _, name -> name.startsWith(timestamp) }
            if (listFiles.isNullOrEmpty()) {
                val invoke = retrofit.create(DownloadByUrlApi::class.java)
                    .invoke(url)

                val name = firebaseStorage.getReferenceFromUrl(url).name

                File(contentPath.path + '/' + contentId).mkdirs()
                val contentDir = File(contentPath.path + '/' + contentId, name)
                contentDir.createNewFile()
                val fileOutputStream = FileOutputStream(contentDir)
                fileOutputStream.write(invoke.body()!!.bytes())
                fileOutputStream.close()
                Attachment(file = contentDir)
            } else {
                Attachment(file = listFiles[0])
            }
        }
    }
}