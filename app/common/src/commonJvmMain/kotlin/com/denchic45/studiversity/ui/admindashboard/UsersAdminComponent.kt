package com.denchic45.studiversity.ui.admindashboard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.*
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.domain.model.UserItem
import com.denchic45.studiversity.domain.usecase.RemoveUserUseCase
import com.denchic45.studiversity.ui.confirm.ConfirmDialogInteractor
import com.denchic45.studiversity.ui.confirm.ConfirmState
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.navigator.RootNavigator
import com.denchic45.studiversity.ui.profile.ProfileComponent
import com.denchic45.studiversity.ui.search.UserChooserComponent
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.studiversity.ui.usereditor.UserEditorComponent
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class UsersAdminComponent(
    private val confirmDialogInteractor: ConfirmDialogInteractor,
    private val removeUserUseCase: RemoveUserUseCase,
    userChooserComponent: (onSelect: (UserItem) -> Unit, ComponentContext) -> UserChooserComponent,
    userEditorComponent: (UUID?, onFinish: () -> Unit, ComponentContext) -> UserEditorComponent,
    profileComponent: (
        RootNavigator,
        UUID, ComponentContext,
    ) -> ProfileComponent,
    @Assisted
    private val rootNavigation: StackNavigation<RootConfig>,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext, SearchableAdminComponent<UserItem> {
    private val componentScope = componentScope()
    private val slotNavigation = SlotNavigation<Config>()
    val childSlot: Value<ChildSlot<Config, Child>> = childSlot(
        source = slotNavigation,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                is Config.UserEditor -> Child.UserEditor(
                    userEditorComponent(config.userId, slotNavigation::dismiss, context)
                )
            }
        })

    override val chooserComponent = userChooserComponent(::onSelect, componentContext)

    override fun onSelect(item: UserItem) {
        rootNavigation.push(RootConfig.Profile(item.id))
    }

    override fun onEditClick(id: UUID) {
        slotNavigation.activate(Config.UserEditor(id))
    }

    override fun onRemoveClick(id: UUID) {
        componentScope.launch {
            val confirm = confirmDialogInteractor.confirmRequest(
                ConfirmState(
                    uiTextOf("Удалить пользователя?"),
                    uiTextOf("Удалятся все данные, связанные с данным пользователем. Восстановить пользователя будет невозможно.")
                )
            )
            if (confirm) {
                removeUserUseCase(id)
            }
        }
    }

    override fun onAddClick() {
        slotNavigation.activate(Config.UserEditor(null))
    }

    @Parcelize
    sealed interface Config : Parcelable {
        data class UserEditor(val userId: UUID?) : Config
    }

    sealed interface Child {
        class UserEditor(val component: UserEditorComponent) : Child
    }
}