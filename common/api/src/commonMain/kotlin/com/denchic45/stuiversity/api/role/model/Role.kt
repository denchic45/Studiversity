package com.denchic45.stuiversity.api.role.model

import kotlinx.serialization.Serializable

@Serializable
data class Role(val id: Long, val resource: String) {

    override fun toString(): String = resource

    companion object {
        val Guest: Role = Role(1, "guest")
        val User: Role = Role(2, "user")
        val Moderator: Role = Role(3, "moderator")
        val StudentPerson: Role = Role(4, "student_person")
        val TeacherPerson: Role = Role(5, "teacher_person")
        val Student: Role = Role(6, "student")
        val Teacher: Role = Role(7, "teacher")
        val StudyGroupStudent: Role = Role(8, "study_group_student")
        val Headman: Role = Role(9, "headman")
        val Curator: Role = Role(10, "curator")
    }
}