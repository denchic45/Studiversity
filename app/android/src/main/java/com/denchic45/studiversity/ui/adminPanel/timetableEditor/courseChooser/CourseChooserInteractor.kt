package com.denchic45.studiversity.ui.adminPanel.timetableEditor.courseChooser

import com.denchic45.stuiversity.api.course.model.CourseResponse
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseChooserInteractor @Inject constructor() {
    private val selected = Channel<CourseResponse>()

    suspend fun receive(): CourseResponse {
        return selected.receive()
    }


    suspend fun emit(course: CourseResponse) {
        selected.send(course)
    }
}