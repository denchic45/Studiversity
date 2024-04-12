package com.denchic45.studiversity.ui.admindashboard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.denchic45.studiversity.api.room.model.RoomResponse
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.usecase.RemoveRoomUseCase
import com.denchic45.studiversity.ui.roomeditor.RoomEditorComponent
import com.denchic45.studiversity.ui.search.RoomChooserComponent
import com.denchic45.studiversity.util.UUIDSerializer
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class RoomsAdminComponent(
    private val removeRoomUseCase: RemoveRoomUseCase,
    roomChooserComponent: (onSelect: (RoomResponse?) -> Unit, ComponentContext) -> RoomChooserComponent,
    roomEditorComponent: (onFinish: (Boolean) -> Unit, UUID?, ComponentContext) -> RoomEditorComponent,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext,
    AdminSearchableComponent<RoomResponse> {

    private val componentScope = componentScope()
    override val searchableComponent = roomChooserComponent({ room -> room?.let(::onSelect) }, componentContext)

    private val slotNavigation = SlotNavigation<Config>()
    val childSlot = childSlot(
        source = slotNavigation,
        serializer = Config.serializer(),
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                is Config.RoomEditor -> Child.RoomEditor(
                    roomEditorComponent(
                        { edited ->
                            slotNavigation.dismiss()
                            if (edited) searchableComponent.forceUpdate()
                        },
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
        slotNavigation.activate(Config.RoomEditor(null))
    }

    override fun onEditClick(id: UUID) {
        slotNavigation.activate(Config.RoomEditor(id))
    }

    override fun onRemoveClick(id: UUID) {
        componentScope.launch {
            removeRoomUseCase(id).onSuccess { searchableComponent.forceUpdate() }
        }
    }

    @Serializable
    sealed interface Config {
        @Serializable
        data class RoomEditor(
            @Serializable(UUIDSerializer::class)
            val roomId: UUID?
        ) : Config
    }

    sealed interface Child {
        class RoomEditor(val component: RoomEditorComponent) : Child
    }
}