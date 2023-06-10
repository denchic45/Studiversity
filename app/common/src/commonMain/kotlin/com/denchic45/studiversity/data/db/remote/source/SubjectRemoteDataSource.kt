package com.denchic45.studiversity.data.db.remote.source

import com.denchic45.studiversity.data.db.remote.model.CourseMap
import com.denchic45.studiversity.data.db.remote.model.SubjectMap
import com.denchic45.studiversity.util.FireMap
import kotlinx.coroutines.flow.Flow

expect class SubjectRemoteDataSource {

    suspend fun add(subjectMap: SubjectMap)

    suspend fun isExistWithSameIconAndColor(subjectMap: SubjectMap)

    suspend fun update(subjectMap: SubjectMap)

    suspend fun remove(map: FireMap)

    fun observeById(id: String): Flow<SubjectMap?>

    suspend fun findById(id: String): SubjectMap

    suspend fun findByGroupId(groupId: String): List<CourseMap>

    fun findByContainsName(text: String): Flow<List<SubjectMap>>
}