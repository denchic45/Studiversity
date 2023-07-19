package com.denchic45.studiversity.feature.course

import com.denchic45.studiversity.eventsource.Event
import com.denchic45.studiversity.eventsource.EventChannel
import java.util.*

class CoursesChannel {
    companion object : EventChannel<CourseEvent>()
}

sealed class CourseEvent : Event {
    data class CourseAdded(val courseId: UUID) : CourseEvent()
}