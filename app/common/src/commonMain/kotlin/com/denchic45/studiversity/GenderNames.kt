package com.denchic45.studiversity

import com.denchic45.stuiversity.api.user.model.Gender

fun Gender.displayName() = when(this) {
    Gender.UNKNOWN -> "Неизвестно"
    Gender.FEMALE -> "Женский"
    Gender.MALE -> "Мужской"
}