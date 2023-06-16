package com.denchic45.studiversity

import com.denchic45.stuiversity.api.role.model.Role

fun Role.name() = when(this) {
    Role.User -> "Студент"
    Role.Student -> "Студент"
    Role.Teacher -> "Преподаватель"
    Role.Moderator -> "Модератор"
    Role.Headman -> "Староста"
    Role.Curator -> "Куратор"
    else -> "Неизвестная роль"
}