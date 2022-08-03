package com.denchic45.kts.data.remote.model

import com.denchic45.kts.util.FireMap
import com.denchic45.kts.util.SearchKeysGenerator
import java.util.*

data class GroupMap(override val map: FireMap):MapWrapper {

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

    val students: Map<String, FireMap>
        get() = (map["students"] as Map<String, FireMap>?) ?: emptyMap()

    val allUsers: List<FireMap>
        get() = students.values + (curator)
}