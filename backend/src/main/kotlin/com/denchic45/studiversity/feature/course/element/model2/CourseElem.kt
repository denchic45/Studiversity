package com.denchic45.studiversity.feature.course.element.model2

//@Serializable
//sealed class CourseElem {
//    abstract val type: CourseElementType
//    abstract val details: CourseElementDetails
//}
//
//@Serializable
//data class CourseWork @OptIn(ExperimentalSerializationApi::class) constructor(
//    @Serializable(LocalDateSerializer::class)
//    val dueDate: LocalDate?,
//    @Serializable(LocalTimeSerializer::class)
//    val dueTime: LocalTime?,
//    val workType: CourseWorkType,
//    val maxGrade: Short,
//    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
//    override val details: CourseElementDetails,
//    override val type: CourseElementType
//) : CourseElem()
//
//@Serializable
//data class CourseMaterial(
//    val text: String,
//    override val details: CourseElementDetails,
//    override val type: CourseElementType
//) : CourseElem()