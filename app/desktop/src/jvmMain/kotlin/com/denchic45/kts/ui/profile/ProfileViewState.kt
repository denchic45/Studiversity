package com.denchic45.kts.ui.profile

import com.denchic45.stuiversity.api.user.model.UserResponse

data class ProfileViewState(
    val fullName: String,
    val avatarUrl: String,
    val personalDate: PersonalData?,
) {
    data class PersonalData(val email: String)
}

fun UserResponse.toProfileViewState() = ProfileViewState(
    fullName = fullName,
    avatarUrl = avatarUrl,
    personalDate = account.email.let { ProfileViewState.PersonalData(it) })