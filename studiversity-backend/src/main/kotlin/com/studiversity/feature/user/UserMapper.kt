package com.studiversity.feature.user

import com.stuiversity.api.auth.model.CreateUserRequest
import com.stuiversity.api.auth.model.SignupRequest

fun SignupRequest.toCreateUser() = CreateUserRequest(
    firstName = firstName,
    surname = surname,
    patronymic = patronymic,
    email = email
)

//fun CreateUserRequest.toUser(id: UUID) = User(
//    id = id,
//    firstName = firstName,
//    surname = surname,
//    patronymic = patronymic,
//    account = Account(email)
//)