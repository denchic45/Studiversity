package com.denchic45.studiversity.ui.root

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.ui.navigation.RootStackChildrenContainer
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.navigator.RootNavigatorComponent
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class YourStudyGroupsRootComponent(
    rootNavigatorComponent: (initialConfiguration: RootConfig, ComponentContext) -> RootNavigatorComponent,
    @Assisted
    private val studyGroupId: UUID,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext,
    RootStackChildrenContainer by rootNavigatorComponent(RootConfig.StudyGroup(studyGroupId), componentContext)