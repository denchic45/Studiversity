package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.StudyGroupRepository
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import me.tatarka.inject.annotations.Inject

@Inject
class FindStudyGroupByContainsNameUseCase(
    studyGroupRepository: StudyGroupRepository,
) : FindByContainsNameUseCase<StudyGroupResponse>(studyGroupRepository)