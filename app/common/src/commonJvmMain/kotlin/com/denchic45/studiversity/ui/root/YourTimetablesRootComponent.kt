package com.denchic45.studiversity.ui.root

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.ui.navigation.RootStackChildrenContainer
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.navigator.RootNavigatorComponent
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class YourTimetablesRootComponent(
    rootNavigatorComponent: (
        initialConfiguration: RootConfig,
        ComponentContext
    ) -> RootNavigatorComponent,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext,
    RootStackChildrenContainer by rootNavigatorComponent(RootConfig.YourTimetables, componentContext)