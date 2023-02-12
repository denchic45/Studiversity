package com.stuiversity.api.timetable.model

import com.stuiversity.api.common.SortOrder
import com.stuiversity.api.common.Sorting
import com.stuiversity.api.common.SortingClass

sealed class SortingPeriods(field: String) : Sorting(field) {

    class Order(override val order: SortOrder = SortOrder.ASC) : SortingPeriods("order")
    class StudyGroup(override val order: SortOrder = SortOrder.ASC) : SortingPeriods("study_group")
    class Course(override val order: SortOrder = SortOrder.ASC) : SortingPeriods("course")
    class Member(override val order: SortOrder = SortOrder.ASC) : SortingPeriods("member")
    class Room(override val order: SortOrder = SortOrder.ASC) : SortingPeriods("room")

    companion object : SortingClass<SortingPeriods>(
        "order" to { Order(it) },
        "study_group" to { StudyGroup(it) },
        "course" to { Course(it) },
        "member" to { Member(it) },
        "room" to { Room(it) }
    )
}