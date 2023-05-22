package com.denchic45.kts.ui.profile

import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.user.model.UserResponse

data class ProfileViewState(
    val fullName: String,
    val avatarUrl: String,
    val personalDate: PersonalData?,

    val studyGroups: List<StudyGroupResponse>,

    val allowEditConfidential: Boolean,
    val allowEditProfile: Boolean,
    val allowUpdateAvatar: Boolean,
) {
    data class PersonalData(val email: String)
}

fun UserResponse.toProfileViewState(studyGroups:List<StudyGroupResponse>,allowEdit: Boolean, isOwned: Boolean) = ProfileViewState(
    fullName = fullName,
    avatarUrl = avatarUrl,
    personalDate = account.email.let { ProfileViewState.PersonalData(it) },
    studyGroups = studyGroups,
    allowEditConfidential = allowEdit,
    allowEditProfile = allowEdit,
    allowUpdateAvatar = isOwned
)