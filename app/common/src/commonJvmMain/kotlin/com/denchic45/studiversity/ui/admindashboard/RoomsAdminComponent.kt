package com.denchic45.studiversity.ui.admindashboard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.domain.usecase.RemoveRoomUseCase
import com.denchic45.studiversity.ui.confirm.ConfirmDialogInteractor
import com.denchic45.studiversity.ui.confirm.ConfirmState
import com.denchic45.studiversity.ui.roomeditor.RoomEditorComponent
import com.denchic45.studiversity.ui.search.RoomChooserComponent
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.room.model.RoomResponse
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class RoomsAdminComponent(
    private val confirmDialogInteractor: ConfirmDialogInteractor,
    private val removeRoomUseCase: RemoveRoomUseCase,
    roomChooserComponent: (onSelect: (RoomResponse) -> Unit, ComponentContext) -> RoomChooserComponent,
    roomEditorComponent: (onFinish: () -> Unit, UUID?, ComponentContext) -> RoomEditorComponent,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext,
    SearchableAdminComponent<RoomResponse> {

    private val componentScope = componentScope()

    override val chooserComponent = roomChooserComponent(::onSelect, componentContext)

    private val overlayNavigation = SlotNavigation<Config>()
    val childSlot = childSlot(source = overlayNavigation,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                is Config.RoomEditor -> Child.RoomEditor(
                    roomEditorComponent(
                        overlayNavigation::dismiss,
                        config.roomId,
                        context
                    )
                )
            }
        })

    override fun onSelect(item: RoomResponse) {
        onEditClick(item.id)
    }

    override fun onAddClick() {
        overlayNavigation.activate(Config.RoomEditor(null))
    }

    override fun onEditClick(id: UUID) {
        overlayNavigation.activate(Config.RoomEditor(id))
    }

    override fun onRemoveClick(id: UUID) {
        componentScope.launch {
            val confirm = confirmDialogInteractor.confirmRequest(
                ConfirmState(
                    uiTextOf("Удалить аудиторию?"),
                    uiTextOf("Занятия с данной аудиторией по-прежнему будут доступны. Восстановить аудиторию будет невозможно.")
                )
            )
            if (confirm) {
                removeRoomUseCase(id)
            }
        }
    }

    @Parcelize
    sealed interface Config : Parcelable {
        data class RoomEditor(val roomId: UUID?) : Config
    }

    sealed interface Child {
        class RoomEditor(val component: RoomEditorComponent) : Child
    }
}