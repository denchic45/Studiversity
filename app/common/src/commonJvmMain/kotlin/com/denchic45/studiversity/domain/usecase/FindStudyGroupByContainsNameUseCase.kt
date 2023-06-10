package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.StudyGroupRepository
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class FindStudyGroupByContainsNameUseCase @Inject constructor(
    studyGroupRepository: StudyGroupRepository,
) : FindByContainsNameUseCase<StudyGroupResponse>(studyGroupRepository)