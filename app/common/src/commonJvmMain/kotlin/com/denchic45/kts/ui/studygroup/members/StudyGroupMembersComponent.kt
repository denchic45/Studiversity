package com.denchic45.kts.ui.studygroup.members

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.model.GroupMembers
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.usecase.AssignUserRoleInScopeUseCase
import com.denchic45.kts.domain.usecase.FindGroupMembersUseCase
import com.denchic45.kts.domain.usecase.RemoveUserRoleFromScopeUseCase
import com.denchic45.kts.domain.usecase.RemoveUserUseCase
import com.denchic45.kts.ui.model.MenuAction
import com.denchic45.kts.ui.model.toUserItem
import com.denchic45.kts.ui.profile.ProfileComponent
import com.denchic45.kts.ui.usereditor.UserEditorComponent
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.role.model.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class StudyGroupMembersComponent(
    findGroupMembersUseCase: FindGroupMembersUseCase,
    private val assignUserRoleInScopeUseCase: AssignUserRoleInScopeUseCase,
    private val removeUserRoleFromScopeUseCase: RemoveUserRoleFromScopeUseCase,
    profileComponent: (UUID, ComponentContext) -> ProfileComponent,
    @Assisted
    private val onMemberOpen: (memberId: UUID) -> Unit,
    @Assisted
    private val studyGroupId: UUID,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

//    private val overlayNavigation = OverlayNavigation<OverlayConfig>()
//
//    val childOverlay = childOverlay(
//        source = overlayNavigation,
//        childFactory = { config, componentContext ->
//            when (config) {
//                is OverlayConfig.Member -> {
//                    OverlayChild.Member(profileComponent(config.memberId, componentContext))
//                }
//
//                is OverlayConfig.UserEditor -> {
//                    OverlayChild.UserEditor(
//                        userEditorComponent(
//                            overlayNavigation::dismiss,
//                            config.userId,
//                            componentContext
//                        )
//                    )
//                }
//            }
//        }
//    )

//    private val navigation = StackNavigation<GroupMembersConfig>()

//    val stack: Value<ChildStack<GroupMembersConfig, GroupMembersChild>> = childStack(
//        source = navigation,
//        initialConfiguration = GroupMembersConfig.Unselected,
//        childFactory = { config, _ ->
//            when (config) {
//                GroupMembersConfig.Unselected -> GroupMembersChild.Unselected
//                is ProfileConfig -> ProfileChild(profileComponent(config.userId, componentContext))
//                is UserEditorConfig -> {
//                    val appBarInteractor = AppBarInteractor()
//                    UserEditorChild(
//                        userEditorComponent(
//                            appBarInteractor,
//                            navigation::pop,
//                            config.userId,
//                            config.role,
//                            componentContext
//                        ),
//                        appBarInteractor
//                    )
//                }
//            }
//        })

    private val componentScope = componentScope()

    val members: StateFlow<Resource<GroupMembers>> = flow {
        emit(findGroupMembersUseCase(studyGroupId))
    }.mapResource { scopeMembers ->
        val curatorMember = scopeMembers.firstOrNull { member -> Role.Curator in member.roles }
        val groupCurator = curatorMember?.user?.toUserItem()
        val students = (curatorMember?.let { scopeMembers - it }
            ?: scopeMembers).map { it.user.toUserItem() }
        GroupMembers(
            groupId = studyGroupId,
            curator = groupCurator,
            headmanId = scopeMembers.find { member -> Role.Headman in member.roles }?.user?.id,
            students = students
        )
    }.stateIn(
        componentScope,
        SharingStarted.Lazily,
        Resource.Loading
    )



    val memberAction = MutableStateFlow<Pair<List<StudentAction>, UUID>?>(null)

//    init {
//        componentScope.launch {
//            selectedMember.collect { userId ->
//                val config = if (userId != null) ProfileConfig(userId)
//                else GroupMembersConfig.Unselected
//                navigation.bringToFront(config)
//            }
//        }
//    }

    fun onMemberSelect(memberId: UUID) {
        onMemberOpen(memberId)
    }



    fun onExpandMemberAction(memberId: UUID) {
        members.value.onSuccess { members ->
            memberAction.update {
                listOf(
                    if (members.headmanId == memberId) StudentAction.RemoveHeadman
                    else StudentAction.SetHeadman
                ) to memberId
            }
        }
    }

    fun onClickMemberAction(action: StudentAction) {
        componentScope.launch {
            when (action) {
                StudentAction.SetHeadman -> assignUserRoleInScopeUseCase(
                    memberAction.value!!.second,
                    Role.Headman.id,
                    studyGroupId
                )

                StudentAction.RemoveHeadman -> removeUserRoleFromScopeUseCase(
                    studyGroupId,
                    Role.Headman.id,
                    studyGroupId
                )
            }
        }
    }

    fun onDismissAction() {
        memberAction.value = null
    }

//    @Parcelize
//    sealed class OverlayConfig : Parcelable {
//
//        data class Member(val memberId: UUID) : OverlayConfig()
//
//        data class UserEditor(val userId: UUID) : OverlayConfig()
//    }
//
//    sealed class OverlayChild {
//
//        class Member(val component: ProfileComponent) : OverlayChild()
//
//        class UserEditor(val component: UserEditorComponent) : OverlayChild()
//    }

    enum class StudentAction(
        override val title: String,
        override val iconName: String? = null,
    ) : MenuAction {
        SetHeadman("Назначить старостой"),
        RemoveHeadman("Лишить прав старосты"),
//        Edit("Редактировать")
    }
}
