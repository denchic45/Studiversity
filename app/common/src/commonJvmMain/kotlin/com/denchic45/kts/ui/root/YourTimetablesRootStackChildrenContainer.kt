package com.denchic45.kts.ui.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.ui.navigation.ChildrenContainerChild
import com.denchic45.kts.ui.navigation.RootStackChildrenContainer
import com.denchic45.kts.ui.yourtimetables.YourTimetablesComponent
import me.tatarka.inject.annotations.Inject

@Inject
class YourTimetablesRootStackChildrenContainer(
    yourTimetablesComponent: (ComponentContext) -> YourTimetablesComponent,
    componentContext: ComponentContext
) : ComponentContext by componentContext,
    RootStackChildrenContainer<YourTimetablesRootStackChildrenContainer.Config, YourTimetablesRootStackChildrenContainer.Child> {

    @Parcelize
    sealed class Config : Parcelable {
        object YourTimetables : Config()
    }

    sealed class Child : ChildrenContainerChild {
        class YourTimetables(override val component: YourTimetablesComponent) : Child()
    }

    override val navigation: StackNavigation<Config> = StackNavigation()

    override val childStack: Value<ChildStack<Config, Child>> = childStack(
        source = navigation,
        initialConfiguration = Config.YourTimetables,
        childFactory = { config, context ->
            when (config) {
                Config.YourTimetables -> {
                    Child.YourTimetables(yourTimetablesComponent(context))
                }
            }
        }
    )
}