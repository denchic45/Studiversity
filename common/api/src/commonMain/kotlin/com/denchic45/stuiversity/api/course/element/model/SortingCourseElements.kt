package com.denchic45.stuiversity.api.course.element.model

import com.denchic45.stuiversity.api.common.SortOrder
import com.denchic45.stuiversity.api.common.Sorting
import com.denchic45.stuiversity.api.common.SortingClass

sealed class SortingCourseElements(field: String) : Sorting(field) {

    class TopicId(override val order: SortOrder = SortOrder.ASC) : SortingCourseElements("topic_id")

    companion object : SortingClass<SortingCourseElements>("topic_id" to { TopicId(it) })
}