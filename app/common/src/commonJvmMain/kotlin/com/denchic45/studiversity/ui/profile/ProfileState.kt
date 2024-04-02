package com.denchic45.studiversity.ui.profile

import com.denchic45.studiversity.domain.model.StudyGroupItem
import com.denchic45.studiversity.api.course.model.CourseResponse
import com.denchic45.studiversity.api.role.model.Role
import com.denchic45.studiversity.api.user.model.UserResponse

data class ProfileState(
    val user: UserResponse,
    val roles: List<Role>,

    val studyGroups: List<StudyGroupItem>,
    val courses: List<CourseResponse>,

    val self: Boolean,
    val allowEditConfidential: Boolean,
    val allowEditProfile: Boolean,
)

fun UserResponse.toProfileState(
    roles: List<Role>,
    studyGroups: List<StudyGroupItem>,
    courses: List<CourseResponse>,
    allowEdit: Boolean,
    self: Boolean
) = ProfileState(
    user = this,
    studyGroups = studyGroups,
    courses = courses,
    self = self,
    allowEditConfidential = allowEdit,
    allowEditProfile = allowEdit,
    roles = roles
)