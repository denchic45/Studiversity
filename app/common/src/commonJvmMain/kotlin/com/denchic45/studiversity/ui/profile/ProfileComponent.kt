package com.denchic45.studiversity.ui.profile

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.data.preference.UserPreferences
import com.denchic45.studiversity.domain.resource.*
import com.denchic45.studiversity.domain.usecase.*
import com.denchic45.studiversity.ui.navigation.EmptyChildrenContainer
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.course.element.model.CreateFileRequest
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.util.toUUID
import com.denchic45.stuiversity.util.uuidOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class ProfileComponent(
    userPreferences: UserPreferences,
    observeUserUseCase: ObserveUserUseCase,
    findStudyGroupsUseCase: FindStudyGroupsUseCase,
    findAssignedUserRolesInScopeUseCase: FindAssignedUserRolesInScopeUseCase,
    private val updateAvatarUseCase: UpdateAvatarUseCase,
    private val removeAvatarUseCase: RemoveAvatarUseCase,
    private val checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    @Assisted
    private val onStudyGroupOpen: (UUID) -> Unit,
    @Assisted
    val userId: UUID,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext, EmptyChildrenContainer {

    private val componentScope = componentScope()

    private val userFlow = observeUserUseCase(userId).stateInResource(componentScope)

    private val userRole = findAssignedUserRolesInScopeUseCase(userId, null)

    private val studyGroups = findStudyGroupsUseCase(memberId = uuidOf(userId))

    private val capabilities = userFlow.flatMapResourceFlow { profileUser ->
        checkUserCapabilitiesInScopeUseCase(
            scopeId = profileUser.id,
            capabilities = listOf(Capability.WriteUser)
        )
    }

    val fullAvatarSize = MutableStateFlow(false)

    private val overlayNavigation = SlotNavigation<OverlayConfig>()

    val childSlot = childSlot(
        source = overlayNavigation,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                OverlayConfig.AvatarDialog -> OverlayChild.AvatarDialog
                OverlayConfig.FullAvatar -> OverlayChild.FullAvatar
                OverlayConfig.ImageChooser -> OverlayChild.AvatarChooser
            }
        }
    )

    val viewState: StateFlow<Resource<ProfileViewState>> = combine(
        userFlow,
        userRole,
        studyGroups,
        capabilities
    ) { userRes, roleRes, studyGroupsRes, capabilitiesRes ->
        bindResources {
            val user = userRes.bind()
            user.toProfileViewState(
                roleRes.bind().roles.single(),
                studyGroupsRes.bind(),
                capabilitiesRes.bind().hasCapability(Capability.WriteUser),
                userPreferences.id.toUUID() == user.id,
            )
        }
    }.stateInResource(componentScope)

    fun onAvatarClick() {
        viewState.value.onSuccess { profile ->
            userFlow.value.onSuccess { user ->
                overlayNavigation.activate(
                    if (profile.self) {
                        if (user.generatedAvatar)
                            OverlayConfig.ImageChooser
                        else
                            OverlayConfig.AvatarDialog
                    } else {
                        OverlayConfig.FullAvatar
                    }
                )
            }
        }
    }


    fun onStudyGroupClick(studyGroupId: UUID) {
        onStudyGroupOpen(studyGroupId)
    }

    fun onDialogClose() {
        overlayNavigation.dismiss()
    }

    fun onOpenAvatarClick() {
        overlayNavigation.activate(OverlayConfig.FullAvatar)
    }

    fun onUpdateAvatarClick() {
        overlayNavigation.activate(OverlayConfig.ImageChooser)
    }

    fun onRemoveAvatarClick() {
        overlayNavigation.dismiss()
        userFlow.value.onSuccess {
            componentScope.launch {
                removeAvatarUseCase(it.id)
            }
        }
    }

    fun onNewAvatarSelect(name: String, bytes: ByteArray) {
        userFlow.value.onSuccess {
            componentScope.launch {
                updateAvatarUseCase(it.id, CreateFileRequest(name, bytes))
            }
            overlayNavigation.dismiss()
        }
    }

    @Parcelize
    sealed class OverlayConfig : Parcelable {
        object AvatarDialog : OverlayConfig()
        object FullAvatar : OverlayConfig()
        object ImageChooser : OverlayConfig()
    }

    sealed class OverlayChild {
        object AvatarDialog : OverlayChild()
        object FullAvatar : OverlayChild()
        object AvatarChooser : OverlayChild()
    }
}