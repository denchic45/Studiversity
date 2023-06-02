package com.denchic45.kts.ui.admindashboard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.overlay.dismiss
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.ui.chooser.CourseChooserComponent
import com.denchic45.kts.ui.chooser.SearchableComponent
import com.denchic45.kts.ui.courseeditor.CourseEditorComponent
import com.denchic45.kts.ui.navigator.RootConfig
import com.denchic45.stuiversity.api.course.model.CourseResponse
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CoursesAdminComponent(
    courseChooserComponent: (onSelect: (CourseResponse) -> Unit, ComponentContext) -> CourseChooserComponent,
    courseEditorComponent: (onFinish: () -> Unit, UUID?, ComponentContext) -> CourseEditorComponent,
    @Assisted
   private val rootNavigation: StackNavigation<RootConfig>,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext,
    SearchableAdminComponent<CourseResponse> {

    override val chooserComponent: CourseChooserComponent = courseChooserComponent(::onSelect, componentContext)

//    private val overlayNavigation = OverlayNavigation<AddCourseConfig>()
//    val childOverlay = childOverlay(source = overlayNavigation,
//        handleBackButton = true,
//        childFactory = { _, context ->
//            AddCourseChild(courseEditorComponent(overlayNavigation::dismiss, null, context))
//        })

    override fun onSelect(item: CourseResponse) {
        rootNavigation.bringToFront(RootConfig.Course(item.id))
    }
    override fun onAddClick() {
        rootNavigation.push(RootConfig.CourseEditor(null))
    }

    override fun onEditClick(id: UUID) {
        rootNavigation.push(RootConfig.CourseEditor(id))
    }
}