package com.denchic45.studiversity.api.course.member

import com.denchic45.studiversity.api.common.SortOrder
import com.denchic45.studiversity.api.common.Sorting
import com.denchic45.studiversity.api.common.SortingClass

sealed class CourseMemberSorting : Sorting() {

    class FullName(override val order: SortOrder = SortOrder.ASC) : CourseMemberSorting()

    class UpperParentRole(override val order: SortOrder) : CourseMemberSorting()

    companion object : SortingClass<CourseMemberSorting>(
        "full_name" to ::FullName,
        "upper_parent_role" to ::UpperParentRole
    )
}