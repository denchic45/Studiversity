//package com.denchic45.studiversity.data.remote.model
//
//import com.denchic45.studiversity.domain.DocModel
//import com.denchic45.studiversity.util.SearchKeysGenerator
//import java.util.*
//
//data class CourseDoc(
//    var id: String,
//    var name: String,
//    val sections: List<SectionMap>?,
//    var subject: SubjectDoc,
//    var teacher: UserDoc,
//    var groupIds: List<String>
//) : DocModel {
//
//    fun equalSubjectsAndTeachers(oldCourseMap: CourseMap) =
//        oldCourseMap.subject != subject
//                && oldCourseMap.teacher != teacher
//
//    val searchKeys: List<String>
//        get() = SearchKeysGenerator().generateKeys(name) { predicate: String -> predicate.length > 1 }
//
//    val timestamp: Date? = null
//
//    private constructor() : this(
//        "", "", emptyList(),
//        SubjectDoc.createEmpty(), UserDoc.createEmpty(), emptyList()
//    )
//}
//
//data class SectionDoc(
//    val id: String,
//    val courseId: String,
//    val name: String,
//    val order: Int
//) : DocModel {
//    private constructor() : this("", "", "", 0)
//}
