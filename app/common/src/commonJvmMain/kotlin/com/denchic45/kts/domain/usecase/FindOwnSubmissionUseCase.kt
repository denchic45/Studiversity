package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.SubmissionRepository
import com.denchic45.kts.data.repository.UserRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionResponse
import java.util.*
import javax.inject.Inject

class FindOwnSubmissionUseCase @Inject constructor(
    private val submissionRepository: SubmissionRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(workId: UUID): Resource<List<SubmissionResponse>> {
        return submissionRepository.findSubmissionsByWork(
            workId,
            userRepository.findSelf().id
        )
    }
}