package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.CourseRepository
import com.denchic45.kts.data.repository.SubjectRepository
import com.denchic45.kts.domain.EmptyResource
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class UnarchiveCourseUseCase @Inject constructor(
    private val courseRepository: CourseRepository,
) {

    suspend operator fun invoke(courseId: UUID): EmptyResource {
        return courseRepository.unarchiveCourse(courseId)
    }
}