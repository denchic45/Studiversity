package com.studiversity.feature.user

import com.denchic45.stuiversity.api.auth.model.SignupRequest
import com.denchic45.stuiversity.api.user.model.Account
import com.denchic45.stuiversity.api.user.model.CreateUserRequest
import com.denchic45.stuiversity.api.user.model.UserResponse
import com.studiversity.database.table.UserDao

fun SignupRequest.toCreateUser() = CreateUserRequest(
    firstName = firstName,
    surname = surname,
    patronymic = patronymic,
    email = email
)

fun UserDao.toUserResponse() = UserResponse(
    id = id.value,
    firstName = firstName,
    surname = surname,
    patronymic = patronymic,
    account = Account(email),
    avatarUrl = avatarUrl,
    gender = gender
)