package com.denchic45.kts.ui.group.courses

import com.denchic45.kts.domain.Interactor
import com.denchic45.kts.domain.model.CourseHeader
import com.denchic45.kts.domain.model.User
import com.denchic45.kts.data.repository.CourseRepository
import com.denchic45.kts.data.repository.GroupRepository
import com.denchic45.kts.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GroupCoursesInteractor @Inject constructor(
    private val groupRepository: GroupRepository
) : Interactor {

    override fun removeListeners() {}

    val yourGroupId: String
        get() = groupRepository.yourGroupId
}