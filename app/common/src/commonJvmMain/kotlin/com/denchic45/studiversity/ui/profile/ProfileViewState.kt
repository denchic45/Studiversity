package com.denchic45.studiversity.ui.profile

import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.role.model.Role
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.user.model.UserResponse

data class ProfileViewState(
    val user: UserResponse,
    val roles: List<Role>,

    val studyGroups: List<StudyGroupResponse>,
    val courses: List<CourseResponse>,

    val self: Boolean,
    val allowEditConfidential: Boolean,
    val allowEditProfile: Boolean,
)

fun UserResponse.toProfileViewState(
    roles: List<Role>,
    studyGroups: List<StudyGroupResponse>,
    courses: List<CourseResponse>,
    allowEdit: Boolean,
    self: Boolean
) = ProfileViewState(
    user = this,
    studyGroups = studyGroups,
    courses = courses,
    self = self,
    allowEditConfidential = allowEdit,
    allowEditProfile = allowEdit,
    roles = roles
)