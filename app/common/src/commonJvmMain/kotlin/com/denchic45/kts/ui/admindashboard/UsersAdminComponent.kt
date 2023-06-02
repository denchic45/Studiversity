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
import com.denchic45.kts.ui.chooser.UserChooserComponent
import com.denchic45.kts.ui.model.UserItem
import com.denchic45.kts.ui.navigator.RootConfig
import com.denchic45.kts.ui.profile.ProfileComponent
import com.denchic45.kts.ui.usereditor.UserEditorComponent
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class UsersAdminComponent(
    userChooserComponent: (onSelect: (UserItem) -> Unit, ComponentContext) -> UserChooserComponent,
    userEditorComponent: (onFinish: () -> Unit, ComponentContext) -> UserEditorComponent,
    profileComponent: (
        onStudyGroupOpen: (UUID) -> Unit,
        UUID, ComponentContext,
    ) -> ProfileComponent,
    @Assisted
    rootNavigation: StackNavigation<RootConfig>,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext, SearchableAdminComponent<UserItem> {
    private val sidebarNavigation = OverlayNavigation<Config>()
    val childOverlay = childOverlay(source = sidebarNavigation,
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

    override fun onAddClick() {
        sidebarNavigation.activate(Config.UserEditor(null))
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