package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseRepository
import com.denchic45.stuiversity.util.UserId
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class FindCoursesUseCase(private val courseRepository: CourseRepository) {
    operator fun invoke(
        memberId: UserId? = null,
        studyGroupId: UUID? = null,
        subjectId: UUID? = null,
        archived: Boolean = false,
        query: String? = null,
    ) = courseRepository.findBy(memberId, studyGroupId, subjectId, archived, query)
}