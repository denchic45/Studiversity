package com.denchic45.studiversity.ui.profile

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.data.preference.UserPreferences
import com.denchic45.studiversity.domain.resource.*
import com.denchic45.studiversity.domain.usecase.*
import com.denchic45.studiversity.ui.main.AppNavigation
import com.denchic45.studiversity.ui.navigation.EmptyChildrenContainer
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.navigator.RootNavigator
import com.denchic45.studiversity.ui.navigator.RootNavigatorComponent
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
    findCoursesUseCase: FindCoursesUseCase,
    findAssignedUserRolesInScopeUseCase: FindAssignedUserRolesInScopeUseCase,
    private val updateAvatarUseCase: UpdateAvatarUseCase,
    private val removeAvatarUseCase: RemoveAvatarUseCase,
    private val checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    @Assisted
    private val navigator: RootNavigator,
    @Assisted
    val userId: UUID,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext, EmptyChildrenContainer {

    private val componentScope = componentScope()

    private val userFlow = observeUserUseCase(userId).stateInResource(componentScope)
    private val userRole = findAssignedUserRolesInScopeUseCase(userId, null)
    private val studyGroups = findStudyGroupsUseCase(memberId = uuidOf(userId))
    private val courses = findCoursesUseCase(memberId = uuidOf(userId))
    private val capabilities = userFlow.flatMapResourceFlow { profileUser ->
        checkUserCapabilitiesInScopeUseCase(
            scopeId = null,
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
        courses,
        capabilities
    ) { userRes, roleRes, studyGroupsRes, coursesRes, capabilitiesRes ->
        bindResources {
            val user = userRes.bind()
            user.toProfileViewState(
                roleRes.bind().roles.single(),
                studyGroupsRes.bind(),
                coursesRes.bind(),
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
        navigator.bringToFront(RootConfig.StudyGroup(studyGroupId))
//        onStudyGroupOpen(studyGroupId)
    }

    fun onCourseClick(courseId: UUID) {
        navigator.bringToFront(RootConfig.Course(courseId))
    }

    fun onMoreCourseClick() {
        TODO("Show all courses by user")
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
        data object AvatarDialog : OverlayConfig() {
            private fun readResolve(): Any = AvatarDialog
        }

        data object FullAvatar : OverlayConfig() {
            private fun readResolve(): Any = FullAvatar
        }

        data object ImageChooser : OverlayConfig() {
            private fun readResolve(): Any = ImageChooser
        }
    }

    sealed class OverlayChild {
        data object AvatarDialog : OverlayChild()
        data object FullAvatar : OverlayChild()
        data object AvatarChooser : OverlayChild()
    }
}