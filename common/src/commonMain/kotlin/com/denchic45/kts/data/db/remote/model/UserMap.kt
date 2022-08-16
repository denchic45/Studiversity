package com.denchic45.kts.data.db.remote.model

import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.util.FireMap
import com.denchic45.kts.util.SearchKeysGenerator
import com.denchic45.kts.util.mapOrNull
import java.util.*

data class UserMap(override val map: FireMap) : MapWrapper {
    val id: String by map
    val firstName: String by map
    val surname: String by map
    val patronymic: String? by mapOrNull()
    val groupId: String? by mapOrNull()
    val role: String by map
    val email: String? by mapOrNull()
    val photoUrl: String by map
    val timestamp: Date by map
    val gender: Int by map
    val generatedAvatar: Boolean by map
    val admin: Boolean by map

    val searchKeys: List<String>
        get() = SearchKeysGenerator().generateKeys(fullName) { predicate: String -> predicate.length > 2 }

    val isTeacher: Boolean
        get() = UserRole.valueOf(role).let {
            it == UserRole.TEACHER || it == UserRole.HEAD_TEACHER
        }

    val isStudent: Boolean
        get() = UserRole.valueOf(role) == UserRole.STUDENT
    val fullName: String
        get() = "$firstName $surname"
}