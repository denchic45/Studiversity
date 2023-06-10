package com.denchic45.studiversity.data.db.local.source

import com.denchic45.studiversity.AppDatabase
import com.denchic45.studiversity.ContentCommentEntity
import com.denchic45.studiversity.ContentCommentEntityQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class ContentCommentLocalDataSource @Inject constructor(db: AppDatabase) {

    private val queries: ContentCommentEntityQueries = db.contentCommentEntityQueries

    suspend fun upsert(contentCommentEntity: ContentCommentEntity) = withContext(Dispatchers.IO) {
        queries.upsert(contentCommentEntity)
    }

    suspend fun upsert(contentCommentEntities: List<ContentCommentEntity>) =
        withContext(Dispatchers.IO) {
            queries.transaction {
                contentCommentEntities.forEach { queries.upsert(it) }
            }
        }

    suspend fun deleteByContentId(id: String) = withContext(Dispatchers.IO) {
        queries.deleteByContentId(id)
    }
}