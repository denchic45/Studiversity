package com.denchic45.studiversity.ui.periodeditor

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.denchic45.studiversity.domain.model.toItem
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class LessonDetailsEditorComponent(
    @Assisted
    state: EditingPeriod,
    @Assisted
    private val overlayNavigation: OverlayNavigation<PeriodEditorComponent.OverlayConfig>,
    @Assisted
    componentContext: ComponentContext,
) : PeriodDetailsEditorComponent<EditingPeriodDetails.Lesson>(
    state,
    EditingPeriodDetails::Lesson,
    componentContext
) {

    fun onCourseChoose() {
        overlayNavigation.activate(PeriodEditorComponent.OverlayConfig.CourseChooser {
            it.let { details.course = it.toItem() }
        })
    }
}