package com.denchic45.kts.ui.group

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.*
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.StackNavigator
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.data.pref.GroupPreferences
import com.denchic45.kts.ui.navigation.*
import com.denchic45.kts.ui.usereditor.UserEditorComponent
import me.tatarka.inject.annotations.Inject


@Inject
class GroupRootComponent(
    private val lazyGroupComponent: (navigator: StackNavigator<in GroupConfig>, groupId: String) -> GroupComponent,
    componentContext: ComponentContext,
    groupPreferences: GroupPreferences,
    userEditorComponent: (onFinish: () -> Unit, config: UserEditorConfig) -> UserEditorComponent,
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

    private val overlayNavigation = OverlayNavigation<GroupOverlayConfig>()
    val childOverlay: Value<ChildOverlay<GroupOverlayConfig, GroupOverlayChild>> = childOverlay(
        source = overlayNavigation,
        handleBackButton = true
    ) { config, _ ->
        when (config) {
            is UserEditorConfig -> UserEditorChild(
                userEditorComponent(
                    navigation::pop,
                    config
                )
            )
        }
    }

    fun onAddStudentClick() {
        overlayNavigation.activate(UserEditorConfig(null, UserRole.STUDENT, groupId))
    }

    fun onDialogDismiss() {
        overlayNavigation.dismiss()
    }
}