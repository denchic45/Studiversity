package com.denchic45.kts.domain.model

import com.denchic45.kts.data.domain.model.EventType

data class Lesson(
    val subject: Subject,
    val teachers: List<User>,
    val task: Task? = null,
) : EventDetails() {

    val isEmpty: Boolean
        get() = subject.isEmpty

    private constructor() : this(Subject.createEmpty(), emptyList(), Task.createEmpty())

    override fun copy(): Lesson {
        return Lesson(subject, teachers, task)
    }

    override val eventType: EventType
        get() = EventType.LESSON

    companion object {
        fun createEmpty(): Lesson {
            return Lesson()
        }
    }
}