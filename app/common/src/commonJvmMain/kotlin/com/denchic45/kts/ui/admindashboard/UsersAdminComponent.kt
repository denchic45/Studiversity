package com.denchic45.kts.ui.admindashboard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.overlay.dismiss
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.ui.chooser.CourseChooserComponent
import com.denchic45.kts.ui.chooser.SearchableComponent
import com.denchic45.kts.ui.chooser.UserChooserComponent
import com.denchic45.kts.ui.model.UserItem
import com.denchic45.kts.ui.usereditor.UserEditorComponent
import com.denchic45.stuiversity.api.course.model.CourseResponse
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class UsersAdminComponent(
    userChooserComponent: (onSelect: (UserItem) -> Unit, ComponentContext) -> UserChooserComponent,
    userEditorComponent: (onFinish: () -> Unit, ComponentContext) -> UserEditorComponent,
    @Assisted
    onSelect: (UserItem) -> Unit,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext, SearchableComponent<UserItem> by userChooserComponent(onSelect, componentContext)  {
    private val overlayNavigation = OverlayNavigation<CoursesAdminComponent.AddCourseConfig>()
    val childOverlay = childOverlay(source = overlayNavigation,
        handleBackButton = true,
        childFactory = { _, context ->
            AddUserChild(
                userEditorComponent(
                    overlayNavigation::dismiss,
                    context
                )
            )
        })

    fun onAddCourseClick() {
        overlayNavigation
    }

    @Parcelize
    object AddUserConfig : Parcelable

    class AddUserChild(val component: UserEditorComponent)
}