package com.denchic45.kts.data.storage

import com.denchic45.kts.data.domain.model.Attachment
import com.denchic45.kts.data.domain.model.AttachmentFile
import com.denchic45.kts.data.storage.local.ContentAttachmentLocalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class ContentAttachmentStorage @Inject constructor(
    private val contentAttachmentLocalStorage: ContentAttachmentLocalStorage,
) {

//    suspend fun getAttachments(contentId: String, urls: List<String>): List<Attachment> {
//        return withContext(Dispatchers.IO) {
//            if (urls.isEmpty())
//                emptyList()
//            else {
//                urls.map { url ->
//                    val name = contentAttachmentRemoteStorage.getAttachmentName(url)
//                    val localFile = contentAttachmentLocalStorage.get(contentId, name)
//                    if (localFile == null) {
//                        AttachmentFile(
//                            contentAttachmentLocalStorage.saveFile(
//                                contentId = contentId,
//                                name = name,
//                                bytes = contentAttachmentRemoteStorage.get(url)
//                            )
//                        )
//                    } else {
//                        AttachmentFile(file = localFile)
//                    }
//                }.also { attachments ->
//                    //TODO удалять локально файлы, отсутствующие в удаленном хранилище
//                    contentAttachmentLocalStorage.getByContent(contentId).forEach { localFile ->
//                        if (!attachments.any { it.name == localFile.name }) {
//                            localFile.delete()
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    suspend fun addContentAttachments(
//        parentId: String,
//        attachments: List<AttachmentFile>,
//    ): List<String> {
//        return contentAttachmentRemoteStorage.addContentAttachments(parentId, attachments)
//    }
//
//    suspend fun update(id: String, attachments: List<AttachmentFile>): List<String> {
//        return contentAttachmentRemoteStorage.update(id, attachments)
//    }
//
//    suspend fun deleteFilesByContentId(contentId: String) {
//        deleteFromLocal(contentId)
//        contentAttachmentRemoteStorage.deleteByContentId(contentId)
//    }

    fun deleteFromLocal(contentId: String) {
        return contentAttachmentLocalStorage.delete(contentId)
    }
}

