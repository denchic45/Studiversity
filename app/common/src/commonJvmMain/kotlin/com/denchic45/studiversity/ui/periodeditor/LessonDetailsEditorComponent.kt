package com.denchic45.studiversity.ui.periodeditor

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class LessonDetailsEditorComponent(
    @Assisted
    _state: EditingPeriod,
    @Assisted
    private val overlayNavigation: OverlayNavigation<PeriodEditorComponent.OverlayConfig>,
    @Assisted
    componentContext: ComponentContext,
) : PeriodDetailsEditorComponent<EditingPeriodDetails.Lesson>(
    _state,
    EditingPeriodDetails::Lesson,
    componentContext
) {

    fun onCourseChoose() {
        overlayNavigation.activate(PeriodEditorComponent.OverlayConfig.CourseChooser {
            it.let { details.course = it }
        })
    }
}