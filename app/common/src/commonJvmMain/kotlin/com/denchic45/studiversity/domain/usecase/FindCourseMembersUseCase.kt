package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.api.member.ScopeMembers
import com.denchic45.studiversity.data.repository.CourseMemberRepository
import com.denchic45.studiversity.domain.resource.Resource
import me.tatarka.inject.annotations.Inject
import java.util.*


@Inject
class FindCourseMembersUseCase(
    private val courseMemberRepository: CourseMemberRepository
) {

    suspend operator fun invoke(scopeId: UUID): Resource<ScopeMembers> {
        return courseMemberRepository.findMembersByCourseId(scopeId)
    }
}