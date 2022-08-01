package com.denchic45.kts.data.remote.model

import com.denchic45.kts.util.FireMap
import com.denchic45.kts.util.SearchKeysGenerator
import java.util.*

data class CourseMap(val map: FireMap) {
    val id: String by map
    val name: String by map
    val sections: List<FireMap>? by map
    val subject: FireMap by map
    val teacher: FireMap by map
    val groupIds: List<String> by map

    fun equalSubjectsAndTeachers(oldCourseMap: CourseMap) =
        oldCourseMap.subject != subject
                && oldCourseMap.teacher != teacher

    val searchKeys: List<String>
        get() = SearchKeysGenerator().generateKeys(name) { predicate: String -> predicate.length > 1 }

    val timestamp: Date by map
}

data class SectionMap(val map: FireMap) {
    val id: String by map
    val courseId: String by map
    val name: String by map
    val order: Int by map
}