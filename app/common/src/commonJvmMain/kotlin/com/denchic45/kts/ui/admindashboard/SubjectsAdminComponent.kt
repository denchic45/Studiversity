package com.denchic45.kts.ui.admindashboard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.overlay.dismiss
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.ui.chooser.SubjectChooserComponent
import com.denchic45.kts.ui.navigator.RootConfig
import com.denchic45.kts.ui.subjecteditor.SubjectEditorComponent
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class SubjectsAdminComponent(
    subjectChooserComponent: (onSelect: (SubjectResponse) -> Unit, ComponentContext) -> SubjectChooserComponent,
    subjectEditorComponent: (onFinish: () -> Unit, UUID?, ComponentContext) -> SubjectEditorComponent,
    @Assisted
    private val rootNavigation: StackNavigation<RootConfig>,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext,
    SearchableAdminComponent<SubjectResponse> {

    override val chooserComponent: SubjectChooserComponent =
        subjectChooserComponent(::onSelect, componentContext)

    private val sidebarNavigation = OverlayNavigation<Config>()
    val childSidebar = childOverlay(source = sidebarNavigation,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                is Config.SubjectEditor -> Child.SubjectEditor(
                    subjectEditorComponent(
                        sidebarNavigation::dismiss,
                        config.subjectId,
                        context
                    )
                )
            }
        })

    override fun onSelect(item: SubjectResponse) {
        rootNavigation.bringToFront(RootConfig.Course(item.id))
    }

    override fun onAddClick() {
        rootNavigation.push(RootConfig.CourseEditor(null))
    }

    override fun onEditClick(id: UUID) {
        rootNavigation.push(RootConfig.CourseEditor(id))
    }

    @Parcelize
    sealed interface Config : Parcelable {
        data class SubjectEditor(val subjectId: UUID?) : Config
    }

    sealed interface Child {
        class SubjectEditor(val component: SubjectEditorComponent) : Child
    }
}