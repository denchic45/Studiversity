package com.denchic45.kts.data.db.remote.source

import com.denchic45.kts.data.db.remote.model.CourseMap
import com.denchic45.kts.data.db.remote.model.SubjectMap
import com.denchic45.kts.util.FireMap
import kotlinx.coroutines.flow.Flow

@me.tatarka.inject.annotations.Inject
actual class SubjectRemoteDataSource {

    actual suspend fun add(subjectMap: SubjectMap) {

    }

    actual suspend fun isExistWithSameIconAndColor(subjectMap: SubjectMap) {
    }

    actual suspend fun update(subjectMap: SubjectMap) {
    }

    actual fun observeById(id: String): Flow<SubjectMap> {
        TODO("Not yet implemented")
    }

    actual suspend fun remove(map: FireMap) {

    }

    actual suspend fun findById(id: String): SubjectMap {
        TODO("Not yet implemented")
    }

    actual suspend fun findByGroupId(groupId: String): List<CourseMap> {
        TODO("Not yet implemented")
    }

    actual fun findByContainsName(text: String): Flow<List<SubjectMap>> {
        TODO("Not yet implemented")
    }
}