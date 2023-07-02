package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.SubjectRepository
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import me.tatarka.inject.annotations.Inject

@Inject
class FindSubjectByContainsNameUseCase(
    subjectRepository: SubjectRepository,
) : FindByContainsNameUseCase<SubjectResponse>(subjectRepository)