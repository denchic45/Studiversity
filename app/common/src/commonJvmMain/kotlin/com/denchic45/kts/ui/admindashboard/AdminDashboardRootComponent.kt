package com.denchic45.kts.ui.admindashboard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.ui.navigation.ChildrenContainerChild
import com.denchic45.kts.ui.navigation.RootStackChildrenContainer
import com.denchic45.kts.ui.navigator.RootChild
import com.denchic45.kts.ui.navigator.RootConfig
import com.denchic45.kts.ui.navigator.RootNavigatorComponent
import com.denchic45.kts.ui.yourtimetables.YourTimetablesComponent
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class AdminDashboardRootComponent(
//    yourTimetablesRootComponent: (ComponentContext) -> YourTimetablesComponent,
    private val rootNavigatorComponent: (initialConfiguration: RootConfig, ComponentContext) -> RootNavigatorComponent,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext,
    RootStackChildrenContainer by rootNavigatorComponent(
        RootConfig.AdminDashboard,
        componentContext
    ) {

//    @Parcelize
//    sealed class Config : Parcelable {
//        object AdminDashboard : Config()
//        data class Course(val courseId: UUID)
//    }

//    sealed class Child : ChildrenContainerChild {
//        class YourTimetables(override val component: YourTimetablesComponent) : Child()
//    }

//    override val navigation: StackNavigation<Config> = StackNavigation()

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