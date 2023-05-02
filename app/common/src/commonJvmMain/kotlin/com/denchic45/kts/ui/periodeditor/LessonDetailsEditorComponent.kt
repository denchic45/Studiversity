package com.denchic45.kts.ui.periodeditor

import com.arkivanov.decompose.ComponentContext
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.timetable.model.PeriodMember
import com.denchic45.stuiversity.api.timetable.model.toPeriodMember
import com.denchic45.stuiversity.api.user.model.UserResponse
import me.tatarka.inject.annotations.Inject

@Inject
class LessonDetailsEditorComponent(
    _state: EditingPeriod,
    private val _onCourseChoose: () -> Unit,
    componentContext: ComponentContext
) : PeriodDetailsEditorComponent<EditingPeriodDetails.Lesson>(_state, componentContext) {

    fun onCourseChoose() {
        _onCourseChoose()
    }

    fun onCourseSelect(courseResponse: CourseResponse) {
        details.course = courseResponse
    }


}