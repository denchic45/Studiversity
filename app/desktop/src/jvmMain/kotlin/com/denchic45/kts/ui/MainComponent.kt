package com.denchic45.kts.ui

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.ChildOverlay
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.overlay.dismiss
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.denchic45.kts.domain.MainInteractor
import com.denchic45.kts.domain.usecase.RemoveUserUseCase
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.navigation.ConfirmChild
import com.denchic45.kts.ui.navigation.ConfirmConfig
import com.denchic45.kts.ui.navigation.OverlayChild
import com.denchic45.kts.ui.navigation.OverlayConfig
import com.denchic45.kts.ui.navigation.UserEditorChild
import com.denchic45.kts.ui.navigation.UserEditorConfig
import com.denchic45.kts.ui.usereditor.UserEditorComponent
import com.denchic45.kts.ui.yourstudygroups.YourStudyGroupsComponent
import com.denchic45.kts.ui.yourtimetables.YourTimetablesComponent
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.role.model.Role
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class MainComponent constructor(
    private val removeUserUseCase: RemoveUserUseCase,
    private val _yourTimetablesComponent: (ComponentContext) -> YourTimetablesComponent,
    private val _yourStudyGroupsComponent: (ComponentContext) -> YourStudyGroupsComponent,
    mainInteractor: MainInteractor,
    private val overlayNavigation: OverlayNavigation<OverlayConfig>,
    userEditorComponent: (
        onFinish: () -> Unit,
        userId: UUID?,
        ComponentContext
    ) -> UserEditorComponent,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    data class DialogConfig(val title: String) : Parcelable

    private val navigation = StackNavigation<Config>()

    val stack: Value<ChildStack<Config, Child>> = childStack(
        source = navigation,
        initialConfiguration = Config.YourTimetables,
        childFactory = { config, componentContext ->
            when (config) {
                is Config.YourTimetables -> Child.YourTimetables(
                    _yourTimetablesComponent(
                        componentContext
                    )
                )

                is Config.YourStudyGroups -> Child.YourStudyGroups(
                    _yourStudyGroupsComponent(
                        componentContext
                    )
                )
            }
        })

    val childOverlay: Value<ChildOverlay<OverlayConfig, OverlayChild>> = childOverlay(
        source = overlayNavigation,
        handleBackButton = true
    ) { config, _ ->
        when (config) {
            is UserEditorConfig -> {
                val appBarInteractor = AppBarInteractor()
                UserEditorChild(
                    userEditorComponent(
                        appBarInteractor,
                        overlayNavigation::dismiss,
                        config.userId,
                        null,
                        componentContext
                    ),
                    appBarInteractor
                )
            }

            is ConfirmConfig -> ConfirmChild(config)
        }
    }

    init {
        componentScope.launch { mainInteractor.startListeners() }
//        coroutineScope.launch { mainInteractor.observeHasGroup() }
    }

    fun onTimetableClick() {
        navigation.bringToFront(Config.YourTimetables)
    }

    fun onGroupClick() {
        navigation.bringToFront(Config.YourStudyGroups)
    }

    fun onOverlayDismiss() {
        overlayNavigation.dismiss()
    }

    sealed class Config : Parcelable {
        object YourTimetables : Config()
        object YourStudyGroups : Config()
    }

    sealed class Child {
        abstract val component: ComponentContext

        class YourTimetables(override val component: YourTimetablesComponent) : Child()

        class YourStudyGroups(override val component: YourStudyGroupsComponent) : Child()
    }
}
