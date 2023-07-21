package com.denchic45.studiversity.ui.root

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.ui.navigation.RootStackChildrenContainer
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.navigator.RootNavigatorComponent
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class YourTimetablesRootComponent(
//    yourTimetablesRootComponent: (ComponentContext) -> YourTimetablesComponent,
    private val rootNavigatorComponent: (initialConfiguration: RootConfig,ComponentContext)->RootNavigatorComponent,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext,
    RootStackChildrenContainer by rootNavigatorComponent(RootConfig.YourTimetables,componentContext) {

//    @Parcelize
//    sealed class Config : Parcelable {
//        object YourTimetables : Config()
//    }

//    sealed class Child : ChildrenContainerChild {
//        class YourTimetables(override val component: YourTimetablesComponent) : Child()
//    }

//    override val navigation: StackNavigation<Config> = StackNavigation()
//
//    override val childStack: Value<ChildStack<Config, Child>> = childStack(
//        source = navigation,
//        initialConfiguration = Config.YourTimetables,
//        childFactory = { config, context ->
//            when (config) {
//                Config.YourTimetables -> {
//                    Child.YourTimetables(yourTimetablesRootComponent(context))
//                }
//            }
//        }
//    )
}