package com.denchic45.stuiversity.api.course.model

import com.denchic45.stuiversity.api.common.FieldFilter
import com.denchic45.stuiversity.api.common.FieldFilterClass
import com.denchic45.stuiversity.util.toUUID
import java.util.*

sealed class CourseFieldFilter<T>(field: String) : FieldFilter<T>(field) {
    class MemberId(override val value: UUID) : CourseFieldFilter<UUID>("member_id")

    companion object : FieldFilterClass<CourseFieldFilter<*>>(
        "member_id" to { MemberId(it.toUUID()) }
    )
}

