package com.denchic45.studiversity.domain.model

import com.denchic45.stuiversity.api.user.model.Gender
import java.util.UUID

data class UserDomain(
    override var id: UUID,
    val firstName: String,
    val surname: String,
    val patronymic: String?,
    val email: String,
    val photoUrl: String,
    val gender: Gender,
) : DomainModel {

//    private constructor() : this(
//        ,
//        "",
//        "",
//        "",
//        "",
//        Gender.UNKNOWN
//    )


//    val isTeacher: Boolean
//        get() = role == UserRole.TEACHER || role == UserRole.HEAD_TEACHER
//    val isStudent: Boolean
//        get() = role == UserRole.STUDENT


//    fun hasAdminPerms() = admin || role == UserRole.HEAD_TEACHER

    val fullName: String
        get() = "$firstName $surname"

    fun isIt(userDomain: UserDomain): Boolean {
        return userDomain.id == id
    }

    override fun copy(): UserDomain {
        return UserDomain(
            id,
            firstName,
            surname,
            patronymic,
            email,
            photoUrl,
            gender
        )
    }

    companion object {
        fun isTeacher(role: UserRole): Boolean {
            return role == UserRole.TEACHER || role == UserRole.HEAD_TEACHER
        }

        fun isStudent(role: UserRole): Boolean {
            return role == UserRole.STUDENT
        }

//        fun createEmpty(): User {
//            return User()
//        }
    }
}