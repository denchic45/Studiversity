package com.denchic45.studiversity.feature.membership.usecase

import com.denchic45.studiversity.feature.membership.repository.UserMembershipRepository
import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.stuiversity.api.membership.model.Member
import java.util.*

class AddUserToMembershipUseCase(
    private val userMembershipRepository: UserMembershipRepository,
    private val roleRepository: RoleRepository
) {
    operator fun invoke(member: Member, roles: List<Long>, scopeId: UUID) {
        userMembershipRepository.addMember(member)
        roleRepository.addUserRolesToScope(member.userId, roles, scopeId)
    }
}