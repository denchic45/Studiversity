package com.denchic45.kts.data.model.firestore

import com.denchic45.kts.data.model.DocModel
import com.denchic45.kts.data.model.domain.User
import com.denchic45.kts.utils.SearchKeysGenerator
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class UserDoc(
    var id: String = UUID.randomUUID().toString(),
    var firstName: String,
    var surname: String,
    var role: User.Role,
    var email: String? = null,
    var photoUrl: String,
    var gender: Int,
    var admin: Boolean,
    @ServerTimestamp
    val timestamp: Date? = null,
    var generatedAvatar: Boolean,
    var groupId: String? = null,
    var patronymic: String? = null

) : DocModel {

    val searchKeys: List<String>
        get() = SearchKeysGenerator().generateKeys(fullName) { predicate: String -> predicate.length > 2 }

    private constructor() : this(
        "",
        "",
        "",
        User.Role.STUDENT,
        "",
        "",
        0,
        false,
        null,
        true,
        "",
        ""
    )

    companion object {
        fun createEmpty() = UserDoc()
    }

    val isTeacher: Boolean
        get() {
            return role == User.Role.TEACHER || role == User.Role.HEAD_TEACHER
        }
    val isStudent: Boolean
        get() = role == User.Role.STUDENT
    val fullName: String
        get() = "$firstName $surname"
}