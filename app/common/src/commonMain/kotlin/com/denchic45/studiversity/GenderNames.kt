package com.denchic45.studiversity

import com.denchic45.studiversity.api.user.model.Gender

fun Gender.displayName() = when(this) {
    Gender.UNKNOWN -> "Не указан"
    Gender.FEMALE -> "Женский"
    Gender.MALE -> "Мужской"
}