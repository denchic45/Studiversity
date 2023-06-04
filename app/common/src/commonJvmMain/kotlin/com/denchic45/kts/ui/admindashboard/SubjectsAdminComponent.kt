package com.denchic45.kts.ui.admindashboard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.overlay.dismiss
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.domain.usecase.RemoveSubjectUseCase
import com.denchic45.kts.ui.confirm.ConfirmDialogInteractor
import com.denchic45.kts.ui.confirm.ConfirmState
import com.denchic45.kts.ui.navigator.RootConfig
import com.denchic45.kts.ui.search.SubjectChooserComponent
import com.denchic45.kts.ui.subjecteditor.SubjectEditorComponent
import com.denchic45.kts.ui.uiTextOf
import com.denchic45.kts.util.componentScope
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

    private val overlayNavigation = OverlayNavigation<Config>()
    val childOverlay = childOverlay(
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