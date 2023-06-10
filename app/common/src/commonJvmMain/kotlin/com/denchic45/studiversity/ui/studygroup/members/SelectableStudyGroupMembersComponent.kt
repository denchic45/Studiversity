package com.denchic45.studiversity.ui.studygroup.members

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.overlay.ChildOverlay
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.ui.profile.ProfileComponent
import kotlinx.coroutines.flow.MutableStateFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class SelectableStudyGroupMembersComponent(
    private val _studyGroupMembersComponent: (
        onMemberOpen: (memberId: UUID) -> Unit,
        UUID,
        ComponentContext,
    ) -> StudyGroupMembersComponent,
    _profileComponent: (UUID, ComponentContext) -> ProfileComponent,
    @Assisted
    private val studyGroupId: UUID,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    val studyGroupMembersComponent = _studyGroupMembersComponent(
        { overlayNavigation.activate(OverlayConfig.Member(it)) },
        studyGroupId,
        componentContext.childContext("Members")
    )
    val selectedMember = MutableStateFlow<UUID?>(null)

    private val overlayNavigation = OverlayNavigation<OverlayConfig>()

    val childOverlay: Value<ChildOverlay<OverlayConfig, OverlayChild>> = childOverlay(
        source = overlayNavigation,
        childFactory = { config, componentContext ->
            when (config) {
                is OverlayConfig.Member -> OverlayChild.Member(_profileComponent(config.memberId, componentContext))
            }
        }
    )

    fun onMemberSelect(memberId: UUID) {
        selectedMember.value = memberId
    }

    fun onCloseProfileClick() {
        selectedMember.value = null
    }

    @Parcelize
    sealed class OverlayConfig : Parcelable {

        data class Member(val memberId: UUID) : OverlayConfig()
    }

    sealed class OverlayChild {

        class Member(val component: ProfileComponent) : OverlayChild()
    }
}