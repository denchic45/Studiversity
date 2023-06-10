package com.denchic45.studiversity.ui.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.ui.navigation.ChildrenContainerChild
import com.denchic45.studiversity.ui.navigation.RootStackChildrenContainer
import com.denchic45.studiversity.ui.navigator.RootChild
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.navigator.RootNavigatorComponent
import com.denchic45.studiversity.ui.yourtimetables.YourTimetablesComponent
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