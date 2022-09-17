package com.denchic45.kts.ui.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.denchic45.kts.data.repository.EventRepository
import com.denchic45.kts.data.service.AuthService
import com.denchic45.kts.domain.MainInteractor
import com.denchic45.kts.ui.timetable.TimetableComponent
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class RootComponent @Inject constructor(
    timetableComponent: () -> TimetableComponent,
    eventRepository: EventRepository,
    mainInteractor: MainInteractor,
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    private val coroutineScope = componentScope()

    private val stack = childStack<Config, Child>(
        source = navigation,
        initialConfiguration = Config.Login,
        childFactory = { config: Config, componentContext: ComponentContext ->
            Child.Timetable(timetableComponent())
        }
    )

    val childStack: Value<ChildStack<*, Child>> = stack

    init {
        coroutineScope.launch { eventRepository.observeEventsOfYourGroup() }
        coroutineScope.launch { mainInteractor.startListeners() }
    }

    private sealed class Config : Parcelable {
        object Login : Config()
    }

    sealed class Child {
        class Timetable(val timetableComponent: TimetableComponent) : Child()
    }
}