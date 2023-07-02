package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.StudyGroupRepository
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class ObserveYourStudyGroupIdsUseCase(
    private val studyGroupRepository: StudyGroupRepository
) {
    operator fun invoke(): Flow<List<UUID>> {
        return studyGroupRepository.observeIdsByMe()
    }
}