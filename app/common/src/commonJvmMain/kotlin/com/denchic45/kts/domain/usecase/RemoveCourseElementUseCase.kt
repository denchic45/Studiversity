package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.CourseElementRepository
import com.denchic45.kts.domain.EmptyResource
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class RemoveCourseElementUseCase @Inject constructor(
    private val courseElementRepository: CourseElementRepository,
) {
    suspend operator fun invoke(courseId: UUID, elementId: UUID): EmptyResource {
        return courseElementRepository.removeElement(courseId, elementId)
    }
}