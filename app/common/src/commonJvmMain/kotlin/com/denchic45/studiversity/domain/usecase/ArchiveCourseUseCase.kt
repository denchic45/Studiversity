package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseRepository
import com.denchic45.studiversity.domain.resource.EmptyResource
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class ArchiveCourseUseCase(
    private val courseRepository: CourseRepository,
) {

    suspend operator fun invoke(courseId: UUID): EmptyResource {
        return courseRepository.archiveCourse(courseId)
    }
}