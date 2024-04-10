package com.denchic45.studiversity.feature.studygroup.member.usecase

import com.denchic45.studiversity.api.studygroup.member.StudyGroupMemberSorting
import com.denchic45.studiversity.feature.studygroup.member.StudyGroupMemberRepository
import com.denchic45.studiversity.transaction.TransactionWorker
import java.util.*

class FindStudyGroupMembersUseCase(
    private val transactionWorker: TransactionWorker,
    private val studyGroupMemberRepository: StudyGroupMemberRepository
) {
    operator fun invoke(studyGroupId: UUID, sorting: List<StudyGroupMemberSorting>?) = transactionWorker {
        studyGroupMemberRepository.findByStudyGroupId(studyGroupId, sorting)
    }
}