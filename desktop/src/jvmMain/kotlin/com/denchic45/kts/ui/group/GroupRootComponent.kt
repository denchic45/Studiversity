package com.denchic45.kts.ui.group

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.StackNavigator
import com.arkivanov.decompose.router.stack.childStack
import com.denchic45.kts.data.pref.GroupPreferences
import com.denchic45.kts.ui.navigation.GroupChild
import com.denchic45.kts.ui.navigation.GroupConfig
import me.tatarka.inject.annotations.Inject


@Inject
class GroupRootComponent(
    private val lazyGroupComponent: (navigator: StackNavigator<in GroupConfig>, groupId: String) -> GroupComponent,
    componentContext: ComponentContext,
    groupPreferences: GroupPreferences,
) : ComponentContext by componentContext {

    private val groupId: String = groupPreferences.groupId

    private val groupComponent by lazy { lazyGroupComponent(navigation, groupId) }

    private val navigation = StackNavigation<GroupConfig>()
    val stack = childStack(source = navigation,
        initialConfiguration = GroupConfig.Group(groupId),
        childFactory = { configuration: GroupConfig, _ ->
            when (configuration) {
                is GroupConfig -> GroupChild.Group(groupComponent)
            }
        })
}