package com.denchic45.kts.ui.admindashboard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.overlay.dismiss
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.domain.usecase.RemoveSpecialtyUseCase
import com.denchic45.kts.ui.confirm.ConfirmDialogInteractor
import com.denchic45.kts.ui.confirm.ConfirmState
import com.denchic45.kts.ui.navigator.RootConfig
import com.denchic45.kts.ui.search.SpecialtyChooserComponent
import com.denchic45.kts.ui.specialtyeditor.SpecialtyEditorComponent
import com.denchic45.kts.ui.uiTextOf
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class SpecialtiesAdminComponent(
    private val confirmDialogInteractor: ConfirmDialogInteractor,
    private val removeSpecialtyUseCase: RemoveSpecialtyUseCase,
    specialtyChooserComponent: (onSelect: (SpecialtyResponse) -> Unit, ComponentContext) -> SpecialtyChooserComponent,
    specialtyEditorComponent: (onFinish: () -> Unit, UUID?, ComponentContext) -> SpecialtyEditorComponent,
    @Assisted
    private val rootNavigation: StackNavigation<RootConfig>,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext,
    SearchableAdminComponent<SpecialtyResponse> {

    private val componentScope = componentScope()

    override val chooserComponent: SpecialtyChooserComponent =
        specialtyChooserComponent(::onSelect, componentContext)

    private val overlayNavigation = OverlayNavigation<Config>()
    val childOverlay = childOverlay(source = overlayNavigation,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                is Config.SpecialtyEditor -> Child.SpecialtyEditor(
                    specialtyEditorComponent(
                        overlayNavigation::dismiss,
                        config.specialtyId,
                        context
                    )
                )
            }
        })

    override fun onSelect(item: SpecialtyResponse) {
        onEditClick(item.id)
    }

    override fun onAddClick() {
        overlayNavigation.activate(Config.SpecialtyEditor(null))
    }

    override fun onEditClick(id: UUID) {
        overlayNavigation.activate(Config.SpecialtyEditor(id))
    }

    override fun onRemoveClick(id: UUID) {
        componentScope.launch {
            val confirm = confirmDialogInteractor.confirmRequest(
                ConfirmState(
                    uiTextOf("Удалить специальность?"),
                    uiTextOf("Группы с данной специальностью по-прежнему будут доступны. Восстановить специальность будет невозможно.")
                )
            )
            if (confirm) {
                removeSpecialtyUseCase(id)
            }
        }
    }

    @Parcelize
    sealed interface Config : Parcelable {
        data class SpecialtyEditor(val specialtyId: UUID?) : Config
    }

    sealed interface Child {
        class SpecialtyEditor(val component: SpecialtyEditorComponent) : Child
    }
}