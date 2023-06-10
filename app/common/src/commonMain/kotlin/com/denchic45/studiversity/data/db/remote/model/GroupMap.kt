package com.denchic45.studiversity.data.db.remote.model

import com.denchic45.studiversity.util.FireMap
import com.denchic45.studiversity.util.SearchKeysGenerator
import com.denchic45.studiversity.util.mapNestedMapOrEmpty
import java.util.*

data class GroupMap(private val map: FireMap) : FireMap by map {

    val id: String by map
    val course: Int by map
    val name: String by map
    val curator: FireMap by map
    val timestamp: Date by map
    val timestampCourses: Date by map
    val specialty: SpecialtyMap = SpecialtyMap(map["specialty"] as FireMap)
    val headmanId: String by map

    val searchKeys: List<String>
        get() = SearchKeysGenerator().generateKeys(name)

    val students: Map<String, FireMap> by mapNestedMapOrEmpty()

    val allUsers: List<FireMap>
        get() = students.values + (curator)
}