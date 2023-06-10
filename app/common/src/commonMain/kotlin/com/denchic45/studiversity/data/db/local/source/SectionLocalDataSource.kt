package com.denchic45.studiversity.data.db.local.source

import com.denchic45.studiversity.AppDatabase
import com.denchic45.studiversity.SectionEntity
import com.denchic45.studiversity.SectionEntityQueries
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class SectionLocalDataSource @Inject constructor(db: AppDatabase) {

    private val queries: SectionEntityQueries = db.sectionEntityQueries

    suspend fun upsert(sectionEntity: SectionEntity) = withContext(Dispatchers.IO) {
        queries.upsert(sectionEntity)
    }

    suspend fun upsert(sectionEntities: List<SectionEntity>) = withContext(Dispatchers.IO) {
        queries.transaction {
            sectionEntities.forEach { queries.upsert(it) }
        }
    }

    fun observe(topicId: String) = queries.getById(topicId).asFlow().mapToOne(Dispatchers.IO)

    fun getByCourseId(courseId: String): Flow<List<SectionEntity>> {
        return queries.getByCourseId(courseId).asFlow().mapToList(Dispatchers.IO)
    }

    suspend fun deleteByCourseId(courseId: String) = withContext(Dispatchers.IO) {
        queries.deleteByCourseId(courseId)
    }


//    suspend fun deleteMissing(availableSections: List<String>) = withContext(Dispatchers.IO) {
//        queries.deleteMissing(ListMapper.fromList(availableSections))
//    }
}