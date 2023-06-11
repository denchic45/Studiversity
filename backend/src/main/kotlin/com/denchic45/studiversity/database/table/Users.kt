package com.denchic45.studiversity.database.table

import com.denchic45.studiversity.database.exists
import com.denchic45.studiversity.util.varcharMax
import com.denchic45.stuiversity.api.user.model.Gender
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*


object Users : UUIDTable("user", "user_id") {
    val firstName = varcharMax("first_name")
    val surname = varcharMax("surname")
    val patronymic = varcharMax("patronymic").nullable()
    val email = varcharMax("email")
    val password = varcharMax("password")
    val avatarUrl = text("avatar_url")
    val generatedAvatar = bool("generated_avatar")
    val gender = enumerationByName<Gender>("gender", 10)
}

class UserDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserDao>(Users) {
        fun isExistEmail(email: String): Boolean {
            return table.exists { Users.email eq email }
        }
    }

    var firstName by Users.firstName
    var surname by Users.surname
    var patronymic by Users.patronymic
    var email by Users.email
    var password by Users.password
    var avatarUrl by Users.avatarUrl
    var generatedAvatar by Users.generatedAvatar
    var gender by Users.gender
}