package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.RoleRepository
import java.util.UUID
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class AssignUserRoleInScopeUseCase @Inject constructor(private val roleRepository: RoleRepository) {

    suspend operator fun invoke(userId:UUID,roleId:Long, scopeId: UUID) {
        roleRepository.addUserRole(userId,roleId, scopeId)
    }
}