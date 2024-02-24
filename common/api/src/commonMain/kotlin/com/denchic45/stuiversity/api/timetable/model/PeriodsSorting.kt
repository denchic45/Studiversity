package com.denchic45.stuiversity.api.timetable.model

import com.denchic45.stuiversity.api.common.SortOrder
import com.denchic45.stuiversity.api.common.Sorting
import com.denchic45.stuiversity.api.common.SortingClass

sealed class PeriodsSorting : Sorting() {

    class Order(override val order: SortOrder = SortOrder.ASC) : PeriodsSorting()
    class StudyGroup(override val order: SortOrder = SortOrder.ASC) : PeriodsSorting()
    class Course(override val order: SortOrder = SortOrder.ASC) : PeriodsSorting()
    class Member(override val order: SortOrder = SortOrder.ASC) : PeriodsSorting()
    class Room(override val order: SortOrder = SortOrder.ASC) : PeriodsSorting()

    companion object : SortingClass<PeriodsSorting>(
        "order" to { Order(it) },
        "study_group" to { StudyGroup(it) },
        "course" to { Course(it) },
        "member" to { Member(it) },
        "room" to { Room(it) }
    )
}