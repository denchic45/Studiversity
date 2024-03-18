package com.denchic45.studiversity.data.db.local.source

import com.denchic45.studiversity.entity.AppDatabase
import com.denchic45.studiversity.entity.AttachmentRefEntity
import com.denchic45.studiversity.entity.AttachmentRefEntityQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
class AttachmentReferenceLocalDataSource(db: AppDatabase) {
    private val queries: AttachmentRefEntityQueries = db.attachmentRefEntityQueries

    suspend fun insert(attachmentRefEntity: AttachmentRefEntity) = withContext(Dispatchers.IO) {
        queries.insert(attachmentRefEntity)
    }

    suspend fun insert(attachmentReferenceEntities: List<AttachmentRefEntity>) =
        withContext(Dispatchers.IO) {
            queries.transaction {
                attachmentReferenceEntities.forEach {
                    queries.insert(it)
                }
            }
        }

    suspend fun delete(attachmentId: String, referenceId: String) = withContext(Dispatchers.IO) {
        queries.delete(attachmentId, referenceId)
    }

    suspend fun deleteByNotInIds(actualIds: List<String>, referenceId: String) =
        withContext(Dispatchers.IO) {
            queries.deleteByNotContainsId(actualIds, referenceId)
        }
}