package com.denchic45.kts.data.db.local.source

import com.denchic45.kts.AppDatabase
import com.denchic45.kts.SectionEntity
import com.denchic45.kts.SectionEntityQueries
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
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

    fun observe(topicId: String) = queries.getById(topicId).asFlow().mapToOne()

    fun getByCourseId(courseId: String): Flow<List<SectionEntity>> {
        return queries.getByCourseId(courseId).asFlow().mapToList()
    }

    suspend fun deleteByCourseId(courseId: String) = withContext(Dispatchers.IO) {
        queries.deleteByCourseId(courseId)
    }


//    suspend fun deleteMissing(availableSections: List<String>) = withContext(Dispatchers.IO) {
//        queries.deleteMissing(ListMapper.fromList(availableSections))
//    }
}