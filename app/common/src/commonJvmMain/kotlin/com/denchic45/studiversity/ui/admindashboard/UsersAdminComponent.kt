package com.denchic45.studiversity.ui.admindashboard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.domain.usecase.RemoveUserUseCase
import com.denchic45.studiversity.ui.confirm.ConfirmDialogInteractor
import com.denchic45.studiversity.ui.confirm.ConfirmState
import com.denchic45.studiversity.ui.model.UserItem
import com.denchic45.studiversity.ui.navigator.RootConfig
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
    userEditorComponent: (onFinish: () -> Unit, ComponentContext) -> UserEditorComponent,
    profileComponent: (
        onStudyGroupOpen: (UUID) -> Unit,
        UUID, ComponentContext,
    ) -> ProfileComponent,
    @Assisted
    private val rootNavigation: StackNavigation<RootConfig>,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext, SearchableAdminComponent<UserItem> {
    private val componentScope = componentScope()
    private val sidebarNavigation = SlotNavigation<Config>()
    val childSlot = childSlot(source = sidebarNavigation,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                is Config.UserEditor -> Child.UserEditor(
                    userEditorComponent(
                        sidebarNavigation::dismiss,
                        context
                    )
                )

                is Config.Profile -> Child.Profile(
                    profileComponent(
                        { rootNavigation.bringToFront(RootConfig.StudyGroup(it)) },
                        config.userId,
                        context
                    )
                )
            }
        })

    override val chooserComponent = userChooserComponent(::onSelect, componentContext)

    override fun onSelect(item: UserItem) {
        sidebarNavigation.activate(Config.Profile(item.id))
    }

    override fun onEditClick(id: UUID) {
        sidebarNavigation.activate(Config.UserEditor(id))
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
        sidebarNavigation.activate(Config.UserEditor(null))
    }

    fun onUserClick(userId: UUID) {
//        sidebarNavigation.activate(Config.Profile(userId))
        rootNavigation.push(RootConfig.Profile(userId))
    }

    @Parcelize
    sealed interface Config : Parcelable {
        data class UserEditor(val userId: UUID?) : Config

        data class Profile(val userId: UUID) : Config
    }

    sealed interface Child {
        class UserEditor(val component: UserEditorComponent) : Child

        class Profile(val component: ProfileComponent) : Child
    }
}