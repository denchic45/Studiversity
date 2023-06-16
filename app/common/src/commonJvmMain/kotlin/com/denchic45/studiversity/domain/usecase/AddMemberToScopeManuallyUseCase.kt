package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.MemberRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.membership.model.ScopeMember
import java.util.UUID
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class AddMemberToScopeManuallyUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {

    suspend operator fun invoke(userId: UUID, scopeId: UUID, roleIds: List<Long>): Resource<ScopeMember> {
      return  memberRepository.addByManual(userId, scopeId, roleIds)
    }
}