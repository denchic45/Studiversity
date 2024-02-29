package com.denchic45.studiversity.ui.admindashboard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.domain.usecase.RemoveStudyGroupUseCase
import com.denchic45.studiversity.ui.confirm.ConfirmDialogInteractor
import com.denchic45.studiversity.ui.confirm.ConfirmState
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.search.StudyGroupChooserComponent
import com.denchic45.studiversity.ui.studygroupeditor.StudyGroupEditorComponent
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class StudyGroupsAdminComponent(
    private val confirmDialogInteractor: ConfirmDialogInteractor,
    private val removeStudyGroupUseCase: RemoveStudyGroupUseCase,
    studyGroupChooserComponent: (onSelect: (StudyGroupResponse) -> Unit, ComponentContext) -> StudyGroupChooserComponent,
    studyGroupEditorComponent: (onFinish: () -> Unit, UUID?, ComponentContext) -> StudyGroupEditorComponent,
    @Assisted
    private val rootNavigation: StackNavigation<RootConfig>,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext, SearchableAdminComponent<StudyGroupResponse> {

    private val componentScope = componentScope()

    override val chooserComponent = studyGroupChooserComponent(::onSelect, componentContext)

    private val sidebarNavigation = SlotNavigation<Config>()
    val childSidebar = childSlot(source = sidebarNavigation,
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

    override fun onRemoveClick(id: UUID) {
        componentScope.launch {
            val confirm = confirmDialogInteractor.confirmRequest(
                ConfirmState(
                    uiTextOf("Удалить группу?"),
                    uiTextOf("Все участники лишатся доступа к данной группе. Восстановить группу будет невозможно.")
                )
            )
            if (confirm) {
                removeStudyGroupUseCase(id)
            }
        }
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