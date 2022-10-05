package com.denchic45.kts.ui

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.denchic45.kts.data.pref.GroupPreferences
import com.denchic45.kts.domain.MainInteractor
import com.denchic45.kts.ui.group.GroupComponent
import com.denchic45.kts.ui.timetable.TimetableComponent
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class MainComponent @Inject constructor(
    timetableComponent: () -> TimetableComponent,
    groupComponent: (groupId: String) -> GroupComponent,
    mainInteractor: MainInteractor,
    componentContext: ComponentContext,
    groupPreferences: GroupPreferences,
) : ComponentContext by componentContext {

    private val coroutineScope = componentScope()

    private val navigation = StackNavigation<Config>()

    val stack: Value<ChildStack<Config, Child>> = childStack(source = navigation,
        initialConfiguration = Config.Timetable,
        childFactory = { config: Config, componentContext: ComponentContext ->
            when (config) {
                is Config.Timetable -> Child.Timetable(timetableComponent())
                is Config.Group -> Child.Group(groupComponent(groupPreferences.groupId))
            }
        })

    init {
        coroutineScope.launch { mainInteractor.startListeners() }
        coroutineScope.launch { mainInteractor.observeHasGroup() }
    }

    fun onTimetableClick() {
        navigation.bringToFront(Config.Timetable)
    }

    fun onGroupClick() {
        navigation.bringToFront(Config.Group)
    }

    sealed class Config : Parcelable {
        object Timetable : Config()
        object Group : Config()
    }

    sealed class Child {
        class Timetable(val timetableComponent: TimetableComponent) : Child()
        class Group(val groupComponent: GroupComponent) : Child()
    }
}
