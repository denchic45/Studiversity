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
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.domain.MainInteractor
import com.denchic45.kts.ui.navigation.ConfirmChild
import com.denchic45.kts.ui.navigation.ConfirmConfig
import com.denchic45.kts.ui.navigation.OverlayChild
import com.denchic45.kts.ui.navigation.OverlayConfig
import com.denchic45.kts.ui.navigation.RootStackChildrenContainer
import com.denchic45.kts.ui.navigation.UserEditorChild
import com.denchic45.kts.ui.navigation.UserEditorConfig
import com.denchic45.kts.ui.root.YourStudyGroupsRootStackChildrenContainer
import com.denchic45.kts.ui.root.YourTimetablesRootStackChildrenContainer
import com.denchic45.kts.ui.usereditor.UserEditorComponent
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class MainComponent constructor(
    private val yourTimetablesRootComponent: (ComponentContext) -> YourTimetablesRootStackChildrenContainer,
    private val yourStudyGroupsRootComponent: (ComponentContext) -> YourStudyGroupsRootStackChildrenContainer,
    mainInteractor: MainInteractor,
    userEditorComponent: (
        onFinish: () -> Unit,
        ComponentContext,
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

                RootConfig.Settings -> TODO()
                RootConfig.Works -> TODO()
            }
        })

    private val overlayNavigation: OverlayNavigation<OverlayConfig> = OverlayNavigation()

    val childOverlay: Value<ChildOverlay<OverlayConfig, OverlayChild>> = childOverlay(
        source = overlayNavigation,
        handleBackButton = true
    ) { config, _ ->
        when (config) {
            is UserEditorConfig -> {
                UserEditorChild(
                    userEditorComponent(
                        overlayNavigation::dismiss,
                        componentContext
                    )
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

    @Parcelize
    sealed class RootConfig : Parcelable {

        object YourTimetables : RootConfig()

        object YourStudyGroups : RootConfig()

        object Works : RootConfig()

        object Settings : RootConfig()
    }

    sealed interface PrimaryChild
    sealed interface ExtraChild

    sealed class RootChild {
        abstract val component: RootStackChildrenContainer<*, *>

        class YourTimetables(
            override val component: YourTimetablesRootStackChildrenContainer
        ) : RootChild(), PrimaryChild

        class YourStudyGroups(
            override val component: YourStudyGroupsRootStackChildrenContainer
        ) : RootChild(), PrimaryChild

        class Works(
            override val component: RootStackChildrenContainer<*, *>
        ) : RootChild(), ExtraChild

        class Settings(
            override val component: RootStackChildrenContainer<*, *>
        ) : RootChild(), ExtraChild
    }
}
