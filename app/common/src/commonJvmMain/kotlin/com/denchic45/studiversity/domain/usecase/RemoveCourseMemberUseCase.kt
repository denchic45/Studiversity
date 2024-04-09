package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseMemberRepository
import com.denchic45.studiversity.domain.resource.EmptyResource
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class RemoveCourseMemberUseCase(
    private val courseMemberRepository: CourseMemberRepository
) {

    suspend operator fun invoke(courseId: UUID, memberId: UUID): EmptyResource {
        return courseMemberRepository.removeMember(courseId, memberId)
    }
}