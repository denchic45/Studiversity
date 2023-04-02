package com.denchic45.kts.ui.studygroups

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.domain.usecase.FindYourStudyGroupsUseCase
import com.denchic45.kts.ui.navigation.OverlayConfig
import com.denchic45.kts.ui.studygroup.GroupRootComponent
import kotlinx.coroutines.flow.flow
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class StudyGroupsComponent(
    private val findYourStudyGroupsUseCase: FindYourStudyGroupsUseCase,
    private val overlayNavigator: OverlayNavigation<OverlayConfig>,
    private val lazyGroupRootComponent: (OverlayNavigation<OverlayConfig>, groupId: UUID) -> GroupRootComponent,
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private fun groupRootComponent(groupId: UUID) =
        lazyGroupRootComponent(overlayNavigator, groupId)

    private val groups = flow { emit(findYourStudyGroupsUseCase()) }
    private val navigation = StackNavigation<StudyGroupsConfig>()

    val stack = childStack(
        source = navigation,
        initialConfiguration = StudyGroupsConfig.Empty,
        childFactory = { config, _ ->
            when (config) {
                StudyGroupsConfig.Empty -> StudyGroupsChild.Empty
                is StudyGroupsConfig.Group -> {
                    StudyGroupsChild.Group(groupRootComponent(config.studyGroupId))
                }
            }
        })

}


sealed class StudyGroupsConfig : Parcelable {
    @Parcelize
    object Empty : StudyGroupsConfig()

    @Parcelize
    data class Group(val studyGroupId: UUID) : StudyGroupsConfig()
}

sealed class StudyGroupsChild() {
    object Empty : StudyGroupsChild()
    data class Group(val groupRootComponent: GroupRootComponent) : StudyGroupsChild()
}