package com.denchic45.kts.data.remote.model

import com.denchic45.kts.data.domain.model.DocModel
import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.util.SearchKeysGenerator
import java.util.*

data class UserDoc(
    var id: String = UUID.randomUUID().toString(),
    var firstName: String,
    var surname: String,
    var patronymic: String? = null,
    var groupId: String? = null,
    var role: UserRole,
    var email: String? = null,
    var photoUrl: String,
    //    @ServerTimestamp
    val timestamp: Date? = null,
    var gender: Int,
    var generatedAvatar: Boolean,
    var admin: Boolean,

    ) : DocModel {

    val searchKeys: List<String>
        get() = SearchKeysGenerator().generateKeys(fullName) { predicate: String -> predicate.length > 2 }

    private constructor() : this(
        "",
        "",
        "",
        "",
        "",
        UserRole.STUDENT,
        "",
        "",
        Date(),
        0,
        true,
        false
    )

    companion object {
        fun createEmpty() = UserDoc()
    }

    val isTeacher: Boolean
        get() {
            return role == UserRole.TEACHER || role == UserRole.HEAD_TEACHER
        }
    val isStudent: Boolean
        get() = role == UserRole.STUDENT
    val fullName: String
        get() = "$firstName $surname"
}