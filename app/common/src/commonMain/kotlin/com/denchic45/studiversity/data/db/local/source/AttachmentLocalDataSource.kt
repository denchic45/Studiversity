package com.denchic45.studiversity.data.db.local.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.denchic45.studiversity.entity.AppDatabase
import com.denchic45.studiversity.entity.Attachment
import com.denchic45.studiversity.entity.AttachmentQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
class AttachmentLocalDataSource(db: AppDatabase) {
    private val queries: AttachmentQueries = db.attachmentQueries

    suspend fun upsert(attachment: Attachment, referenceId: String?) =
        withContext(Dispatchers.IO) {
            queries.upsert(attachment)
            referenceId?.let {
                queries.upsertReference(attachment.attachment_id, referenceId)
            }
        }

    suspend fun upsert(attachmentEntities: List<Attachment>) = withContext(Dispatchers.IO) {
        queries.transaction {
            attachmentEntities.forEach {
                queries.upsert(it)
            }
        }
    }

    suspend fun updateSync(attachmentId: String, sync: Boolean) = withContext(Dispatchers.IO) {
        queries.updateSync(sync, attachmentId)
    }

    fun getByDirPath(dirPath: String): Flow<List<Attachment>> {
        return queries.getByDirPath(dirPath).asFlow().mapToList(Dispatchers.IO)
    }

    fun getByReferenceId(referenceId: String): Flow<List<Attachment>> {
        return queries.getByReference(referenceId).asFlow().mapToList(Dispatchers.IO)
    }

    suspend fun getUnreferenced() = withContext(Dispatchers.IO) {
        queries.getUnreferenced().executeAsList()
    }

    suspend fun delete(id: String) = withContext(Dispatchers.IO) {
        queries.delete(id)
    }
}
