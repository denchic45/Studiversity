package com.denchic45.studiversity.ui.admindashboard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.*
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.domain.model.StudyGroupItem
import com.denchic45.studiversity.domain.usecase.RemoveStudyGroupUseCase
import com.denchic45.studiversity.ui.confirm.ConfirmDialogInteractor
import com.denchic45.studiversity.ui.confirm.ConfirmState
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.search.StudyGroupChooserComponent
import com.denchic45.studiversity.ui.studygroupeditor.StudyGroupEditorComponent
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class StudyGroupsAdminComponent(
    private val confirmDialogInteractor: ConfirmDialogInteractor,
    private val removeStudyGroupUseCase: RemoveStudyGroupUseCase,
    studyGroupChooserComponent: (onSelect: (StudyGroupItem) -> Unit, ComponentContext) -> StudyGroupChooserComponent,
    studyGroupEditorComponent: (onFinish: () -> Unit, UUID?, ComponentContext) -> StudyGroupEditorComponent,
    @Assisted
    private val rootNavigation: StackNavigation<RootConfig>,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext, SearchableAdminComponent<StudyGroupItem> {
    private val componentScope = componentScope()

    override val chooserComponent = studyGroupChooserComponent(::onSelect, componentContext)

    private val slotNavigation = SlotNavigation<Config>()
    val childSlot: Value<ChildSlot<Config, Child>> = childSlot(source = slotNavigation,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                is Config.StudyGroupEditor -> Child.StudyGroupEditor(
                    studyGroupEditorComponent({ slotNavigation.dismiss() }, config.studyGroupId, context)
                )
            }
        })

    override fun onSelect(item: StudyGroupItem) {
        rootNavigation.bringToFront(RootConfig.StudyGroup(item.id))
    }

    override fun onEditClick(id: UUID) {
        slotNavigation.activate(Config.StudyGroupEditor(id))
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
        slotNavigation.activate(Config.StudyGroupEditor(null))
    }

    @Parcelize
    sealed interface Config : Parcelable {
        data class StudyGroupEditor(val studyGroupId: UUID?) : Config
    }

    sealed interface Child {
        class StudyGroupEditor(val component: StudyGroupEditorComponent) : Child
    }
}