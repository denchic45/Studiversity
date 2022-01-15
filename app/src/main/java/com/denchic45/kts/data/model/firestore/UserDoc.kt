package com.denchic45.kts.data.model.firestore

import com.denchic45.kts.data.model.DocModel
import com.denchic45.kts.data.model.domain.User
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class UserDoc(
    var id: String = UUID.randomUUID().toString(),
    var firstName: String,
    var surname: String,
    var role: String,
    var phoneNum: String,
    var email: String? = null,
    var photoUrl: String,
    var gender: Int,
    var admin: Boolean,
    @ServerTimestamp
    val timestamp: Date? = null,
    var searchKeys: List<String>,
    var generatedAvatar: Boolean,
    var groupId: String? = null,
    var patronymic: String?=null

) : DocModel {

    private constructor():this("", "","","","","", "",0,false, null, emptyList(), true, "", "")

    companion object {
        fun createEmpty() = UserDoc()
    }
    val isTeacher: Boolean
        get() = role == User.TEACHER || role == User.HEAD_TEACHER
    val isStudent: Boolean
        get() = role == User.STUDENT || role == User.DEPUTY_MONITOR || role == User.CLASS_MONITOR
    val fullName: String
        get() = "$firstName $surname"
}