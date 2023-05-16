package com.denchic45.kts.ui.studygroup

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.denchic45.kts.ui.navigation.GroupChild
import com.denchic45.kts.ui.navigation.GroupConfig
import com.denchic45.kts.ui.navigation.OverlayConfig
import com.denchic45.kts.ui.navigation.UserEditorConfig
import com.denchic45.stuiversity.api.role.model.Role
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class YourStudyGroupsRootComponent(
    groupComponent: (groupId: UUID, ComponentContext) -> GroupComponent,
    @Assisted
    private val overlayNavigator: OverlayNavigation<OverlayConfig>,
    @Assisted
    private val groupId: UUID,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<GroupConfig>()

    val stack = childStack(source = navigation,
        initialConfiguration = GroupConfig.Group(groupId),
        childFactory = { configuration: GroupConfig, _ ->
            when (configuration) {
                is GroupConfig.Group -> GroupChild.Group(groupComponent( groupId,componentContext))
            }
        })

    fun onAddStudentClick() {
        overlayNavigator.activate(UserEditorConfig(null, Role.Student))
    }
}