package com.denchic45.stuiversity.api.timetable.model2

import com.denchic45.stuiversity.api.common.SortOrder
import com.denchic45.stuiversity.api.common.Sorting
import com.denchic45.stuiversity.api.common.SortingClass

sealed class ClassesSorting : Sorting() {

    class Order(override val order: SortOrder = SortOrder.ASC) : ClassesSorting()
    class StudyGroup(override val order: SortOrder = SortOrder.ASC) : ClassesSorting()
    class Course(override val order: SortOrder = SortOrder.ASC) : ClassesSorting()
    class Teacher(override val order: SortOrder = SortOrder.ASC) : ClassesSorting()
    class Room(override val order: SortOrder = SortOrder.ASC) : ClassesSorting()

    companion object : SortingClass<ClassesSorting>(
        "order" to { Order(it) },
        "study_group" to { StudyGroup(it) },
        "course" to { Course(it) },
        "teacher" to { Teacher(it) },
        "room" to { Room(it) }
    )
}