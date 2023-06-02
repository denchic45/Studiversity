package com.denchic45.kts.ui.admindashboard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.overlay.dismiss
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.ui.chooser.StudyGroupChooserComponent
import com.denchic45.kts.ui.navigator.RootConfig
import com.denchic45.kts.ui.studygroupeditor.StudyGroupEditorComponent
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class StudyGroupsAdminComponent(
    studyGroupChooserComponent: (onSelect: (StudyGroupResponse) -> Unit, ComponentContext) -> StudyGroupChooserComponent,
    studyGroupEditorComponent: (onFinish: () -> Unit, UUID?, ComponentContext) -> StudyGroupEditorComponent,
    @Assisted
    private val rootNavigation: StackNavigation<RootConfig>,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext, SearchableAdminComponent<StudyGroupResponse> {

    override val chooserComponent = studyGroupChooserComponent(::onSelect, componentContext)

    private val sidebarNavigation = OverlayNavigation<Config>()
    val childSidebar = childOverlay(source = sidebarNavigation,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                is Config.StudyGroupEditor -> Child.StudyGroupEditor(
                    studyGroupEditorComponent(
                        { sidebarNavigation.dismiss() },
                        config.studyGroupId,
                        context
                    )
                )
            }
        })

    override fun onSelect(item: StudyGroupResponse) {
        rootNavigation.bringToFront(RootConfig.StudyGroup(item.id))
    }

    override fun onEditClick(id: UUID) {
        sidebarNavigation.activate(Config.StudyGroupEditor(id))
    }

    override fun onAddClick() {
        sidebarNavigation.activate(Config.StudyGroupEditor(null))
    }

    @Parcelize
    sealed interface Config : Parcelable {
        data class StudyGroupEditor(val studyGroupId: UUID?) : Config
    }

    sealed interface Child {
        class StudyGroupEditor(val component: StudyGroupEditorComponent) : Child
    }
}