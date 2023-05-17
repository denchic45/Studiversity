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
import com.denchic45.kts.ui.root.YourStudyGroupsRootComponent
import com.denchic45.kts.ui.root.YourTimetablesRootComponent
import com.denchic45.kts.ui.usereditor.UserEditorComponent
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class MainComponent constructor(
    private val yourTimetablesRootComponent: (ComponentContext) -> YourTimetablesRootComponent,
    private val yourStudyGroupsRootComponent: (ComponentContext) -> YourStudyGroupsRootComponent,
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

    private val navigation = StackNavigation<RootConfig>()

    val stack: Value<ChildStack<RootConfig, RootChild>> = childStack(
        source = navigation,
        initialConfiguration = RootConfig.YourTimetables,
        childFactory = { config, componentContext ->
            when (config) {
                is RootConfig.YourTimetables -> RootChild.YourTimetables(
                    yourTimetablesRootComponent(
                        componentContext
                    )
                )

                is RootConfig.YourStudyGroups -> RootChild.YourStudyGroups(
                    yourStudyGroupsRootComponent(
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
                        overlayNavigation::dismiss,
                        config.userId,
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
        navigation.bringToFront(RootConfig.YourTimetables)
    }

    fun onGroupClick() {
        navigation.bringToFront(RootConfig.YourStudyGroups)
    }

    fun onOverlayDismiss() {
        overlayNavigation.dismiss()
    }

    sealed class RootConfig : Parcelable {
        object YourTimetables : RootConfig()
        object YourStudyGroups : RootConfig()
    }

    sealed class RootChild {
        abstract val component: RootComponent<*,*>

        class YourTimetables(override val component: YourTimetablesRootComponent) : RootChild()

        class YourStudyGroups(override val component: YourStudyGroupsRootComponent) : RootChild()
    }
}
