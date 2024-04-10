package com.denchic45.studiversity.ui.yourworks

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.ui.course.CourseComponent.TabConfig
import com.denchic45.studiversity.ui.navigation.EmptyChildrenContainer
import com.denchic45.studiversity.ui.navigator.RootNavigator
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@OptIn(ExperimentalDecomposeApi::class)
@Inject
class YourCourseWorksComponent(
    yourUpcomingWorksComponent: (RootNavigator, ComponentContext) -> YourUpcomingWorksComponent,
    yourOverdueWorksComponent: (RootNavigator, ComponentContext) -> YourOverdueWorksComponent,
    yourSubmittedWorksComponent: (RootNavigator, ComponentContext) -> YourSubmittedWorksComponent,
    @Assisted
    private val rootNavigator: RootNavigator,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext, EmptyChildrenContainer {

    private val pageNavigation = PagesNavigation<TabConfig>()
    val childPages = childPages(
        source = pageNavigation,
        initialPages = {
            Pages(
                items = listOf(TabConfig.Upcoming, TabConfig.Overdue, TabConfig.Submitted),
                selectedIndex = 0
            )
        },
        childFactory = { config, context ->
            when (config) {
                TabConfig.Upcoming -> TabChild.Upcoming(
                    yourUpcomingWorksComponent(
                        rootNavigator,
                        context
                    )
                )

                TabConfig.Overdue -> TabChild.Overdue(
                    yourOverdueWorksComponent(
                        rootNavigator,
                        context
                    )
                )

                TabConfig.Submitted -> TabChild.Submitted(
                    yourSubmittedWorksComponent(
                        rootNavigator,
                        context
                    )
                )

            }
        }
    )

    fun onTabSelect(position: Int) {
        pageNavigation.select(position)
    }

    fun onBack() {
        rootNavigator.pop()
    }

    @Parcelize
    sealed class TabConfig : Parcelable {
        data object Upcoming : TabConfig() {
            private fun readResolve(): Any = Upcoming
        }

        data object Overdue : TabConfig() {
            private fun readResolve(): Any = Overdue
        }

        data object Submitted : TabConfig() {
            private fun readResolve(): Any = Submitted
        }
    }

    sealed class TabChild {
        class Upcoming(val component: YourUpcomingWorksComponent) : TabChild()
        class Overdue(val component: YourOverdueWorksComponent) : TabChild()
        class Submitted(val component: YourSubmittedWorksComponent) : TabChild()
    }
}

fun YourCourseWorksComponent.TabConfig.tabTitle() = when (this) {
    YourCourseWorksComponent.TabConfig.Upcoming -> "Предстоящие"
    YourCourseWorksComponent.TabConfig.Overdue -> "Просроченные"
    YourCourseWorksComponent.TabConfig.Submitted -> "Сданные"
}
