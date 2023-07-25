package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseElementRepository
import com.denchic45.studiversity.domain.resource.EmptyResource
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class RemoveCourseElementUseCase(
    private val courseElementRepository: CourseElementRepository,
) {
    suspend operator fun invoke(courseId: UUID, elementId: UUID): EmptyResource {
        return courseElementRepository.removeElement(courseId, elementId)
    }
}