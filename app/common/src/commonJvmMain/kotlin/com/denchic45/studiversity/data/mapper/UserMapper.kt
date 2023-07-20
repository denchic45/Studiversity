package com.denchic45.studiversity.data.mapper

import com.denchic45.studiversity.entity.User
import com.denchic45.stuiversity.api.user.model.Account
import com.denchic45.stuiversity.api.user.model.UserResponse
import com.denchic45.stuiversity.util.toUUID


//fun UserResponse.toUser() = User(
//    id,
//    firstName,
//    surname,
//    patronymic,
//    account.email,
//    avatarUrl,
//    gender,
//)
//
//fun List<UserResponse>.toUsers() = map(UserResponse::toUser)

fun User.toUserResponse() = UserResponse(
    id = user_id.toUUID(),
    firstName = first_name,
    surname = surname,
    patronymic = patronymic,
    account = Account(
        email = email
    ),
    avatarUrl = avatar_url,
    generatedAvatar = generated_avatar,
    gender = gender
)

fun List<User>.toUserResponses() = map(User::toUserResponse)

fun UserResponse.toEntity() = User(
    user_id = id.toString(),
    first_name = firstName,
    surname = surname,
    patronymic = patronymic,
    email = account.email,
    avatar_url = avatarUrl,
    generated_avatar = generatedAvatar,
    gender = gender
)