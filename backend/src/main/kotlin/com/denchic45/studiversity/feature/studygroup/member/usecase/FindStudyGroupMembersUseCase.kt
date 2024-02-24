package com.denchic45.studiversity.feature.studygroup.member.usecase

import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.feature.studygroup.member.StudyGroupMemberRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import com.denchic45.stuiversity.api.studygroup.member.StudyGroupMemberSorting
import java.util.*

class FindStudyGroupMembersUseCase(
    private val transactionWorker: TransactionWorker,
    private val studyGroupMemberRepository: StudyGroupMemberRepository,
    private val roleRepository: RoleRepository
) {
    operator fun invoke(studyGroupId: UUID, sorting: List<StudyGroupMemberSorting>?) = transactionWorker {
        studyGroupMemberRepository.findByStudyGroupId(studyGroupId, sorting)
    }
}