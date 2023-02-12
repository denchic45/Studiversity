package com.stuiversity.api.course.element.model

import com.stuiversity.api.common.SortOrder
import com.stuiversity.api.common.Sorting
import com.stuiversity.api.common.SortingClass

sealed class SortingCourseElements(field: String) : Sorting(field) {

    class TopicId(override val order: SortOrder = SortOrder.ASC) : SortingCourseElements("topic_id")

    companion object : SortingClass<SortingCourseElements>("topic_id" to { TopicId(it) })
}