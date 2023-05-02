package com.denchic45.kts.ui.periodeditor

import com.arkivanov.decompose.ComponentContext
import com.denchic45.stuiversity.api.course.model.CourseResponse
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class LessonDetailsEditorComponent(
    @Assisted
    _state: EditingPeriod,
    @Assisted
    private val _onCourseChoose: (PeriodEditorComponent.OverlayConfig.CourseChooser) -> Unit,
    @Assisted
    componentContext: ComponentContext,
) : PeriodDetailsEditorComponent<EditingPeriodDetails.Lesson>(_state, componentContext) {

    fun onCourseChoose() {
        _onCourseChoose(PeriodEditorComponent.OverlayConfig.CourseChooser {
            it?.let(::onCourseSelect)
        })
    }

    private fun onCourseSelect(courseResponse: CourseResponse) {
        details.course = courseResponse
    }
}