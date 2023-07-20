package com.denchic45.studiversity.domain.model

//data class Lesson(
//    val subject: SubjectResponse,
//    val teachers: List<UserResponse>,
//    val task: Task? = null,
//) : EventDetails() {
//
//    val isEmpty: Boolean
//        get() = subject.isEmpty
//
//    private constructor() : this(Subject.createEmpty(), emptyList(), Task.createEmpty())
//
//    override fun copy(): Lesson {
//        return Lesson(subject, teachers, task)
//    }
//
//    override val eventType: EventType
//        get() = EventType.LESSON
//
//    companion object {
//        fun createEmpty(): Lesson {
//            return Lesson()
//        }
//    }
//}