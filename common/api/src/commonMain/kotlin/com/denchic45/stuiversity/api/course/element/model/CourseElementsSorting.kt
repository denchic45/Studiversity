package com.denchic45.stuiversity.api.course.element.model

import com.denchic45.stuiversity.api.common.SortOrder
import com.denchic45.stuiversity.api.common.Sorting
import com.denchic45.stuiversity.api.common.SortingClass

sealed class CourseElementsSorting(field: String) : Sorting(field) {

    class TopicId(override val order: SortOrder = SortOrder.ASC) : CourseElementsSorting("topic_id")

    companion object : SortingClass<CourseElementsSorting>("topic_id" to { TopicId(it) })
}