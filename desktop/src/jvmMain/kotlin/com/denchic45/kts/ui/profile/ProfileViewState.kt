package com.denchic45.kts.ui.profile

import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.domain.model.User

data class ProfileViewState(
    val fullName: String,
    val role: UserRole,
    val photoUrl: String,
    val groupInfo: String?,
    val groupClickable: Boolean,
    val personalDate: PersonalData?,
) {
    data class PersonalData(val email: String)
}

fun User.toProfileViewState(groupName: String? = null, groupClickable: Boolean) = ProfileViewState(
    fullName = fullName,
    role = role,
    photoUrl = photoUrl,
    groupInfo = groupName,
    groupClickable = groupClickable,
    personalDate = email?.let { ProfileViewState.PersonalData(it) })