package com.denchic45.studiversity.ui.admindashboard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.domain.usecase.RemoveSubjectUseCase
import com.denchic45.studiversity.ui.confirm.ConfirmDialogInteractor
import com.denchic45.studiversity.ui.confirm.ConfirmState
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.search.SubjectChooserComponent
import com.denchic45.studiversity.ui.subjecteditor.SubjectEditorComponent
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class SubjectsAdminComponent(
    private val confirmDialogInteractor: ConfirmDialogInteractor,
    private val removeSubjectUseCase: RemoveSubjectUseCase,
    subjectChooserComponent: (onSelect: (SubjectResponse) -> Unit, ComponentContext) -> SubjectChooserComponent,
    subjectEditorComponent: (onFinish: () -> Unit, UUID?, ComponentContext) -> SubjectEditorComponent,
    @Assisted
    private val rootNavigation: StackNavigation<RootConfig>,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext,
    SearchableAdminComponent<SubjectResponse> {

    private val componentScope = componentScope()

    override val chooserComponent: SubjectChooserComponent =
        subjectChooserComponent(::onSelect, componentContext)

    private val overlayNavigation = SlotNavigation<Config>()
    val childSlot = childSlot(
        source = overlayNavigation,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                is Config.SubjectEditor -> Child.SubjectEditor(
                    subjectEditorComponent(
                        overlayNavigation::dismiss,
                        config.subjectId,
                        context
                    )
                )
            }
        })

    override fun onSelect(item: SubjectResponse) {
        onEditClick(item.id)
    }

    override fun onAddClick() {
        overlayNavigation.activate(Config.SubjectEditor(null))
    }

    override fun onEditClick(id: UUID) {
        overlayNavigation.activate(Config.SubjectEditor(id))
    }

    override fun onRemoveClick(id: UUID) {
        componentScope.launch {
            val confirmState = ConfirmState(
                uiTextOf("Удалить предмет?"),
                uiTextOf("Восстановить предмет будет невозможно.")
            )
            if (confirmDialogInteractor.confirmRequest(confirmState)) {
                removeSubjectUseCase(id)
            }
        }
    }

    @Parcelize
    sealed interface Config : Parcelable {
        data class SubjectEditor(val subjectId: UUID?) : Config
    }

    sealed interface Child {
        class SubjectEditor(val component: SubjectEditorComponent) : Child
    }
}