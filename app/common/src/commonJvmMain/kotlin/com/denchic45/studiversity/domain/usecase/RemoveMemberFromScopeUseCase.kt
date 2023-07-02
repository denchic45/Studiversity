package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.MemberRepository
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class RemoveMemberFromScopeUseCase(
    private val roleRepository: MemberRepository
) {

    suspend operator fun invoke(userId: UUID, scopeId: UUID) {
        roleRepository.removeByManual(userId, scopeId)
    }
}