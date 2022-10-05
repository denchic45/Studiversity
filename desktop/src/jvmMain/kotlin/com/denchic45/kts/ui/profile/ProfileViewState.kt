package com.denchic45.kts.ui.profile

import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.domain.model.User

data class ProfileViewState(
    val fullName: String,
    val role: UserRole,
    val photoUrl: String,
    val groupInfo: String?,
    val personalDate: PersonalData?,
) {
    data class PersonalData(val email: String)
}

fun User.toProfileViewState(groupName: String? = null) = ProfileViewState(fullName = fullName,
    role = role,
    photoUrl = photoUrl,
    groupInfo = groupName,
    personalDate = email?.let { ProfileViewState.PersonalData(it) })