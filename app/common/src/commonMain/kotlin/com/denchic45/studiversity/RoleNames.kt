package com.denchic45.studiversity

import com.denchic45.stuiversity.api.role.model.Role

fun Role.scopedRoleName() = when (this) {
    Role.User -> "Студент"
    Role.Student -> "Студент"
    Role.Teacher -> "Преподаватель"
    Role.Headman -> "Староста"
    Role.Curator -> "Куратор"
    else -> "Неизвестная роль"
}

fun Role.systemRoleName() = when (this) {
    Role.TeacherPerson -> "Преподаватель"
    Role.StudentPerson -> "Студент"
    Role.Moderator -> "Модератор"
    else -> "Неизвестная роль"
}