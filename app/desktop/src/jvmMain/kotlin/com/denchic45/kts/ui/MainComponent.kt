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
import com.denchic45.kts.ui.navigation.*
import com.denchic45.kts.ui.studygroups.StudyGroupsComponent
import com.denchic45.kts.ui.timetable.TimetableComponent
import com.denchic45.kts.ui.usereditor.UserEditorComponent
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class MainComponent constructor(
    private val _timetableComponent: (ComponentContext) -> TimetableComponent,
    private val _studyGroupsComponent: (ComponentContext) -> StudyGroupsComponent,
    mainInteractor: MainInteractor,
    private val overlayNavigation: OverlayNavigation<OverlayConfig>,
    userEditorComponent: (onFinish: () -> Unit, UserEditorConfig, ComponentContext) -> UserEditorComponent,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val coroutineScope = componentScope()

    data class DialogConfig(val title: String) : Parcelable

    private val navigation = StackNavigation<Config>()

    val stack: Value<ChildStack<Config, Child>> = childStack(
        source = navigation,
        initialConfiguration = Config.Timetable,
        childFactory = { config, componentContext ->
            when (config) {
                is Config.Timetable -> Child.Timetable(_timetableComponent(componentContext))
                is Config.StudyGroups -> Child.StudyGroups(_studyGroupsComponent(componentContext))
            }
        })

    val childOverlay: Value<ChildOverlay<OverlayConfig, OverlayChild>> = childOverlay(
        source = overlayNavigation,
        handleBackButton = true
    ) { config, _ ->
        when (config) {
            is UserEditorConfig -> UserEditorChild(
                userEditorComponent(overlayNavigation::dismiss, config, componentContext)
            )
            is ConfirmConfig -> ConfirmChild(config)
        }
    }

    init {
        coroutineScope.launch { mainInteractor.startListeners() }
//        coroutineScope.launch { mainInteractor.observeHasGroup() }
    }

    fun onTimetableClick() {
        navigation.bringToFront(Config.Timetable)
    }

    fun onGroupClick() {
        navigation.bringToFront(Config.StudyGroups)
    }

    fun onOverlayDismiss() {
        overlayNavigation.dismiss()
    }

    sealed class Config : Parcelable {
        object Timetable : Config()
        object StudyGroups : Config()
    }

    sealed class Child {
        abstract val component: ComponentContext

        class Timetable(override val component: TimetableComponent) : Child()
        class StudyGroups(override val component: StudyGroupsComponent) : Child()
    }
}
