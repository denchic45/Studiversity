package com.denchic45.studiversity.data.remote.model

import com.denchic45.studiversity.domain.model.DocModel
import com.denchic45.studiversity.util.SearchKeysGenerator
import java.util.Date

data class GroupDoc(
    var id: String,
    var course: Int,
    var name: String,
    var curator: UserDoc,
    var timestamp: Date? = null,
    var timestampCourses: Date? = null,
    var specialty: SpecialtyDoc,
    val headmanId: String?,
) : DocModel {

    private constructor() : this(
        "",
        0,
        "",
        UserDoc.createEmpty(),
        null,
        null,
        SpecialtyDoc.createEmpty(),
        null
    )

    val searchKeys: List<String>
        get() = SearchKeysGenerator().generateKeys(name)

    var students: Map<String, UserDoc>? = null
        get() = field ?: emptyMap()


    val allUsers: List<UserDoc>
        get() = students!!.values + (curator)

    companion object {
        fun createEmpty() = GroupDoc()
    }
}