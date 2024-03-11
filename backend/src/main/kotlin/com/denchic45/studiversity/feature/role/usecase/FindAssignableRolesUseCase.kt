package com.denchic45.studiversity.feature.role.usecase

import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.transaction.DatabaseTransactionWorker
import com.denchic45.studiversity.transaction.TransactionWorker

class FindAssignableRolesUseCase(
    private val transactionWorker: TransactionWorker,
    private val roleRepository: RoleRepository) {
operator fun invoke(roleId:Long) = transactionWorker {
    roleRepository.findAssignableRoles(roleId)
}
}
