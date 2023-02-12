package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.CourseRepository
import com.denchic45.kts.domain.model.CourseHeader
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class ObserveCoursesByGroupUseCase @javax.inject.Inject constructor(private val courseRepository: CourseRepository) {
    operator fun invoke(groupId: String): Flow<List<CourseHeader>> {
        return courseRepository.findByGroupId(groupId)
    }
}