package com.denchic45.kts.ui.admindashboard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.overlay.dismiss
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.ui.chooser.CourseChooserComponent
import com.denchic45.kts.ui.chooser.SearchableComponent
import com.denchic45.kts.ui.courseeditor.CourseEditorComponent
import com.denchic45.stuiversity.api.course.model.CourseResponse
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CoursesAdminComponent(
    courseChooserComponent: (onSelect: (CourseResponse) -> Unit, ComponentContext) -> CourseChooserComponent,
    courseEditorComponent: (onFinish: () -> Unit, UUID?, ComponentContext) -> CourseEditorComponent,
    @Assisted
    onSelect: (CourseResponse) -> Unit,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext,
    SearchableComponent<CourseResponse> by courseChooserComponent(onSelect, componentContext) {

    private val overlayNavigation = OverlayNavigation<AddCourseConfig>()
    val childOverlay = childOverlay(source = overlayNavigation,
        handleBackButton = true,
        childFactory = { _, context ->
            AddCourseChild(courseEditorComponent(overlayNavigation::dismiss, null, context))
        })

    fun onAddCourseClick() {
        overlayNavigation
    }

    @Parcelize
    object AddCourseConfig : Parcelable

    class AddCourseChild(val component: CourseEditorComponent)
}