package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.SubjectRepository
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import javax.inject.Inject

class FindSubjectByContainsNameUseCase @Inject constructor(
    subjectRepository: SubjectRepository,
) : FindByContainsNameUseCase<SubjectResponse>(subjectRepository)