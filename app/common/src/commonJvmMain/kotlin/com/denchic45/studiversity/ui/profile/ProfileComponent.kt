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
import com.denchic45.studiversity.domain.model.toItem
import com.denchic45.studiversity.domain.resource.*
import com.denchic45.studiversity.domain.usecase.*
import com.denchic45.studiversity.ui.navigation.EmptyChildrenContainer
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.navigator.RootNavigator
import com.denchic45.studiversity.ui.usercourses.UserCoursesComponent
import com.denchic45.studiversity.ui.userstudygroups.UserStudyGroupsComponent
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.util.toUUID
import com.denchic45.stuiversity.util.userIdOf
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
    userCoursesComponent: (onResult: (UUID?) -> Unit, UUID, ComponentContext) -> UserCoursesComponent,
    userStudyGroupsComponent: (onResult: (UUID?) -> Unit, UUID, ComponentContext) -> UserStudyGroupsComponent,
    private val updateAvatarUseCase: UpdateAvatarUseCase,
    private val removeAvatarUseCase: RemoveAvatarUseCase,
    checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
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
    private val studyGroups = findStudyGroupsUseCase(memberId = userIdOf(userId))
        .mapResource { it.map(StudyGroupResponse::toItem) }
    private val courses = findCoursesUseCase(memberId = userIdOf(userId))
    private val capabilities = checkUserCapabilitiesInScopeUseCase(
        scopeId = null,
        capabilities = listOf(Capability.WriteUser)
    )

//    val fullAvatarSize = MutableStateFlow(false)

    private val slotNavigation = SlotNavigation<SlotConfig>()

    val childSlot = childSlot(
        source = slotNavigation,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                SlotConfig.AvatarDialog -> SlotChild.AvatarDialog
                SlotConfig.FullAvatar -> SlotChild.FullAvatar
                SlotConfig.ImageChooser -> SlotChild.AvatarChooser
                is SlotConfig.UserCourses -> SlotChild.UserCourses(
                    userCoursesComponent({ courseId ->
                        slotNavigation.dismiss()
                        if (courseId != null) navigator.bringToFront(RootConfig.Course(courseId))
                    }, config.userId, context)
                )

                is SlotConfig.UserStudyGroups -> SlotChild.UserStudyGroups(
                    userStudyGroupsComponent({ studyGroupId ->
                        slotNavigation.dismiss()
                        if (studyGroupId != null) navigator.bringToFront(RootConfig.StudyGroup(studyGroupId))
                    }, config.userId, context)
                )
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
                roles = roleRes.bind().roles,
                studyGroups = studyGroupsRes.bind(),
                courses = coursesRes.bind(),
                allowEdit = capabilitiesRes.bind().hasCapability(Capability.WriteUser),
                self = userPreferences.id.toUUID() == user.id,
            )
        }
    }.stateInResource(componentScope)

    fun onAvatarClick() {
        viewState.value.onSuccess { profile ->
            userFlow.value.onSuccess { user ->
                slotNavigation.activate(
                    if (profile.self) {
                        if (user.generatedAvatar)
                            SlotConfig.ImageChooser
                        else
                            SlotConfig.AvatarDialog
                    } else {
                        SlotConfig.FullAvatar
                    }
                )
            }
        }
    }


    fun onStudyGroupClick(studyGroupId: UUID) {
        navigator.bringToFront(RootConfig.StudyGroup(studyGroupId))
    }

    fun onCourseClick(courseId: UUID) {
        navigator.bringToFront(RootConfig.Course(courseId))
    }

    fun onMoreCourseClick() {
        slotNavigation.activate(SlotConfig.UserCourses(userId))
    }

    fun onMoreStudyGroupsClick() {
        slotNavigation.activate(SlotConfig.UserStudyGroups(userId))
    }

    fun onDialogClose() {
        slotNavigation.dismiss()
    }

    fun onOpenAvatarClick() {
        slotNavigation.activate(SlotConfig.FullAvatar)
    }

    fun onUpdateAvatarClick() {
        slotNavigation.activate(SlotConfig.ImageChooser)
    }

    fun onRemoveAvatarClick() {
        slotNavigation.dismiss()
        userFlow.value.onSuccess {
            componentScope.launch {
                removeAvatarUseCase(it.id)
            }
        }
    }

    fun onNewAvatarSelect(name: String, bytes: ByteArray) {
        userFlow.value.onSuccess {
            componentScope.launch {
                TODO("Pass input stream")
//                updateAvatarUseCase(it.id, CreateFileRequest(name, bytes))
            }
            slotNavigation.dismiss()
        }
    }


    @Parcelize
    sealed class SlotConfig : Parcelable {
        data object AvatarDialog : SlotConfig() {
            private fun readResolve(): Any = AvatarDialog
        }

        data object FullAvatar : SlotConfig() {
            private fun readResolve(): Any = FullAvatar
        }

        data object ImageChooser : SlotConfig() {
            private fun readResolve(): Any = ImageChooser
        }

        data class UserCourses(val userId: UUID) : SlotConfig()

        data class UserStudyGroups(val userId: UUID) : SlotConfig()
    }

    sealed class SlotChild {
        data object AvatarDialog : SlotChild()
        data object FullAvatar : SlotChild()
        data object AvatarChooser : SlotChild()
        class UserCourses(val component: UserCoursesComponent) : SlotChild()
        class UserStudyGroups(val component: UserStudyGroupsComponent) : SlotChild()
    }
}