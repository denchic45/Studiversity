package com.denchic45.kts.ui.studygroup.courses

import com.denchic45.kts.domain.Interactor
import com.denchic45.kts.data.repository.StudyGroupRepository
import javax.inject.Inject

class GroupCoursesInteractor @Inject constructor(
    private val studyGroupRepository: StudyGroupRepository
) : Interactor {

    override fun removeListeners() {}

    val yourGroupId: String
        get() = studyGroupRepository.yourGroupId
}