package com.denchic45.kts.data.db.local.source

import com.denchic45.kts.AppDatabase
import com.denchic45.kts.AttachmentEntity
import com.denchic45.kts.AttachmentEntityQueries
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AttachmentLocalDataSource(db: AppDatabase) {
    private val queries: AttachmentEntityQueries = db.attachmentEntityQueries

    suspend fun upsert(attachmentEntity: AttachmentEntity) = withContext(Dispatchers.IO) {
        queries.upsert(attachmentEntity)
    }

    suspend fun upsert(attachmentEntities: List<AttachmentEntity>) = withContext(Dispatchers.IO) {
        queries.transaction {
            attachmentEntities.forEach {
                queries.upsert(it)
            }
        }
    }

    suspend fun updateSync(attachmentId: String, sync: Boolean) = withContext(Dispatchers.IO) {
        queries.updateSync(sync, attachmentId)
    }

    fun getByDirPath(dirPath: String): Flow<List<AttachmentEntity>> {
        return queries.getByDirPath(dirPath).asFlow().mapToList(Dispatchers.IO)
    }

    fun getByReferenceId(referenceId: String): Flow<List<AttachmentEntity>> {
        return queries.getByReference(referenceId).asFlow().mapToList()
    }

    suspend fun getUnreferenced() = withContext(Dispatchers.IO) {
        queries.getUnreferenced().executeAsList()
    }

    suspend fun delete(id: String) = withContext(Dispatchers.IO) {
        queries.delete(id)
    }
}