package com.studiversity.feature.course.element.model2

import com.stuiversity.api.course.element.model.CourseElementType
import com.stuiversity.api.course.work.model.CourseWorkType
import com.stuiversity.util.LocalDateSerializer
import com.stuiversity.util.LocalTimeSerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

@Serializable
sealed class CourseElem {
    abstract val type: CourseElementType
    abstract val details: CourseElementDetails
}

@Serializable
data class CourseWork @OptIn(ExperimentalSerializationApi::class) constructor(
    @Serializable(LocalDateSerializer::class)
    val dueDate: LocalDate?,
    @Serializable(LocalTimeSerializer::class)
    val dueTime: LocalTime?,
    val workType: CourseWorkType,
    val maxGrade: Short,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val details: CourseElementDetails,
    override val type: CourseElementType
) : CourseElem()

@Serializable
data class CourseMaterial(
    val text: String,
    override val details: CourseElementDetails,
    override val type: CourseElementType
) : CourseElem()