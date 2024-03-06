package com.denchic45.studiversity.ui.profile

import com.denchic45.stuiversity.api.role.model.Role
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.user.model.UserResponse

data class ProfileViewState(
    val user: UserResponse,
    val role: Role,

    val studyGroups: List<StudyGroupResponse>,

    val self: Boolean,
    val allowEditConfidential: Boolean,
    val allowEditProfile: Boolean,
) {
    data class PersonalData(val email: String)
}

fun UserResponse.toProfileViewState(
    role: Role,
    studyGroups: List<StudyGroupResponse>,
    allowEdit: Boolean,
    self: Boolean
) = ProfileViewState(
    user = this,
    studyGroups = studyGroups,
    self = self,
    allowEditConfidential = allowEdit,
    allowEditProfile = allowEdit,
    role = role
)