package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.SubjectRepository
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class FindSubjectByContainsNameUseCase @Inject constructor(
    subjectRepository: SubjectRepository,
) : FindByContainsNameUseCase<SubjectResponse>(subjectRepository)