package com.denchic45.studiversity.feature.membership.usecase

import com.denchic45.stuiversity.api.membership.model.Member
import com.denchic45.studiversity.feature.membership.repository.UserMembershipRepository
import com.denchic45.studiversity.feature.role.repository.RoleRepository
import java.util.*

class RemoveUserFromMembershipUseCase(
    private val userMembershipRepository: UserMembershipRepository,
    private val roleRepository: RoleRepository
) {
    operator fun invoke(member: Member, scopeId: UUID) {
        userMembershipRepository.removeMember(member)
        roleRepository.removeUserRolesFromScope(member.userId, scopeId)
    }
}