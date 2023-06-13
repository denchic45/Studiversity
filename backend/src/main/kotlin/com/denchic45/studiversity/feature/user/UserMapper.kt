package com.denchic45.studiversity.feature.user

import com.denchic45.studiversity.database.table.UserDao
import com.denchic45.stuiversity.api.auth.model.SignupRequest
import com.denchic45.stuiversity.api.user.model.Account
import com.denchic45.stuiversity.api.user.model.CreateUserRequest
import com.denchic45.stuiversity.api.user.model.UserResponse

fun SignupRequest.toCreateUser() = CreateUserRequest(
    firstName = firstName,
    surname = surname,
    patronymic = patronymic,
    email = email,
    gender = gender,
    listOf()
)

fun UserDao.toUserResponse() = UserResponse(
    id = id.value,
    firstName = firstName,
    surname = surname,
    patronymic = patronymic,
    account = Account(email),
    avatarUrl = avatarUrl,
    gender = gender,
    generatedAvatar = generatedAvatar
)