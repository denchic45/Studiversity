package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseTopicRepository
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class RemoveCourseTopicUseCase(private val courseTopicRepository: CourseTopicRepository) {
    suspend operator fun invoke(
        topicId: UUID,
        withElements: Boolean,
    ) {
        courseTopicRepository.remove(topicId, withElements)
    }
}