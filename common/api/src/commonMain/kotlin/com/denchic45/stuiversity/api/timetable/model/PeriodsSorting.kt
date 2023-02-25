package com.denchic45.stuiversity.api.timetable.model

import com.denchic45.stuiversity.api.common.SortOrder
import com.denchic45.stuiversity.api.common.Sorting
import com.denchic45.stuiversity.api.common.SortingClass

sealed class PeriodsSorting(field: String) : Sorting(field) {

    class Order(override val order: SortOrder = SortOrder.ASC) : PeriodsSorting("order")
    class StudyGroup(override val order: SortOrder = SortOrder.ASC) : PeriodsSorting("study_group")
    class Course(override val order: SortOrder = SortOrder.ASC) : PeriodsSorting("course")
    class Member(override val order: SortOrder = SortOrder.ASC) : PeriodsSorting("member")
    class Room(override val order: SortOrder = SortOrder.ASC) : PeriodsSorting("room")

    companion object : SortingClass<PeriodsSorting>(
        "order" to { Order(it) },
        "study_group" to { StudyGroup(it) },
        "course" to { Course(it) },
        "member" to { Member(it) },
        "room" to { Room(it) }
    )
}