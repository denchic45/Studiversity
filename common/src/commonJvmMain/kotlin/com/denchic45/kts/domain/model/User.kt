package com.denchic45.kts.domain.model

import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.domain.DomainModel
import com.denchic45.kts.util.UUIDS
import java.util.*

data class User(
    override var id: String = UUIDS.createShort(),
    val firstName: String,
    val surname: String,
    val patronymic: String?,
    val groupId: String?,
    val role: UserRole,
    val email: String?,
    val photoUrl: String,
    val timestamp: Date?,
    val gender: Int,
    val generatedAvatar: Boolean,
    val admin: Boolean
) : DomainModel {

    private constructor() : this(
        "",
        "",
        "",
        "",
        "",
        UserRole.STUDENT,
        "",
        "",
        null,
        0,
        false,
        false
    )


    val isTeacher: Boolean
        get() = role == UserRole.TEACHER || role == UserRole.HEAD_TEACHER
    val isStudent: Boolean
        get() = role == UserRole.STUDENT


    fun hasGroup(): Boolean = !groupId.isNullOrEmpty()

    fun hasAdminPerms() = admin || role == UserRole.HEAD_TEACHER

    val fullName: String
        get() = "$firstName $surname"
    val isCurator: Boolean
        get() = isTeacher && hasGroup()

    fun isIt(user: User): Boolean {
        return user.id == id
    }

//    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
//    @StringDef(STUDENT, DEPUTY_MONITOR, CLASS_MONITOR, TEACHER, HEAD_TEACHER)
//    annotation class Role

    override fun copy(): User {
        return User(
            id,
            firstName,
            surname,
            patronymic,
            groupId,
            role,
//            phoneNum,
            email,
            photoUrl,
            timestamp,
            gender,
            generatedAvatar,
            admin
        )
    }

    fun curatorFor(groupId: String): Boolean = isTeacher && groupId == this.groupId

    companion object {
//        const val STUDENT = "STUDENT"

        //        const val DEPUTY_MONITOR = "DEPUTY_MONITOR"
//        const val CLASS_MONITOR = "CLASS_MONITOR"
//        const val TEACHER = "TEACHER"
//        const val HEAD_TEACHER = "HEAD_TEACHER"

        fun isTeacher(role: UserRole): Boolean {
            return role == UserRole.TEACHER || role == UserRole.HEAD_TEACHER
        }

        fun isStudent(role: UserRole): Boolean {
            return role == UserRole.STUDENT
        }

        fun createEmpty(): User {
            return User()
        }
    }
}