package com.denchic45.stuiversity.api.studygroup.member

import com.denchic45.stuiversity.api.common.SortOrder
import com.denchic45.stuiversity.api.common.Sorting
import com.denchic45.stuiversity.api.common.SortingClass

sealed class StudyGroupMemberSorting : Sorting() {

    class FullName(override val order: SortOrder = SortOrder.ASC) : StudyGroupMemberSorting()

    class UpperParentRole(override val order: SortOrder) : StudyGroupMemberSorting()

    companion object : SortingClass<StudyGroupMemberSorting>(
        "full_name" to ::FullName,
        "upper_parent_role" to ::UpperParentRole
    )
}