package com.denchic45.kts.ui.group

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.dismiss
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.StackNavigator
import com.arkivanov.decompose.router.stack.childStack
import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.data.pref.GroupPreferences
import com.denchic45.kts.ui.navigation.GroupChild
import com.denchic45.kts.ui.navigation.GroupConfig
import com.denchic45.kts.ui.navigation.OverlayConfig
import com.denchic45.kts.ui.navigation.UserEditorConfig
import me.tatarka.inject.annotations.Inject


@Inject
class GroupRootComponent(
    lazyGroupComponent: (navigator: StackNavigator<in GroupConfig>, groupId: String) -> GroupComponent,
    private val overlayNavigator: OverlayNavigation<OverlayConfig>,
    componentContext: ComponentContext,
    groupPreferences: GroupPreferences,
) : ComponentContext by componentContext {

    private val groupId: String = groupPreferences.groupId

    private val navigation = StackNavigation<GroupConfig>()

    private val groupComponent = lazyGroupComponent(navigation, groupId)

    val stack = childStack(source = navigation,
        initialConfiguration = GroupConfig.Group(groupId),
        childFactory = { configuration: GroupConfig, _ ->
            when (configuration) {
                is GroupConfig -> GroupChild.Group(groupComponent)
            }
        })

    fun onAddStudentClick() {
        overlayNavigator.activate(UserEditorConfig(null, UserRole.STUDENT, groupId))
    }
}