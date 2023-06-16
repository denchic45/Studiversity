package com.denchic45.studiversity.ui.studygroup.members

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.mapResource
import com.denchic45.studiversity.domain.model.GroupMembers
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.domain.stateInResource
import com.denchic45.studiversity.domain.usecase.AssignUserRoleInScopeUseCase
import com.denchic45.studiversity.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.studiversity.domain.usecase.FindGroupMembersUseCase
import com.denchic45.studiversity.domain.usecase.RemoveMemberFromScopeUseCase
import com.denchic45.studiversity.domain.usecase.RemoveUserRoleFromScopeUseCase
import com.denchic45.studiversity.ui.confirm.ConfirmDialogInteractor
import com.denchic45.studiversity.ui.confirm.ConfirmState
import com.denchic45.studiversity.ui.model.MenuAction
import com.denchic45.studiversity.ui.model.toUserItem
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.api.role.model.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class StudyGroupMembersComponent(
    findGroupMembersUseCase: FindGroupMembersUseCase,
    checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    private val assignUserRoleInScopeUseCase: AssignUserRoleInScopeUseCase,
    private val removeUserRoleFromScopeUseCase: RemoveUserRoleFromScopeUseCase,
    private val removeMemberFromScopeUseCase: RemoveMemberFromScopeUseCase,
    private val confirmDialogInteractor: ConfirmDialogInteractor,
    @Assisted
    private val onMemberOpen: (memberId: UUID) -> Unit,
    @Assisted
    private val onMemberEdit: (memberId: UUID) -> Unit,
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

    private val checkUserCapabilities = checkUserCapabilitiesInScopeUseCase(
        scopeId = studyGroupId,
        capabilities = listOf(Capability.WriteStudyGroup)
    ).shareIn(componentScope, SharingStarted.Lazily)

    val allowEditMembers = checkUserCapabilities
        .mapResource { it.hasCapability(Capability.WriteStudyGroup) }
        .stateInResource(componentScope)

    val members: StateFlow<Resource<GroupMembers>> = flow {
        emit(findGroupMembersUseCase(studyGroupId))
    }.mapResource { scopeMembers ->
        val curatorMember = scopeMembers.firstOrNull { member -> Role.Curator in member.roles }
        val groupCurator = curatorMember?.user?.toUserItem()
        val students = (curatorMember?.let { scopeMembers - it }
            ?: scopeMembers).map {
            it.user.toUserItem(if (it.roles.contains(Role.Headman)) "Староста" else null)
        }
        GroupMembers(
            groupId = studyGroupId,
            curator = groupCurator,
            headmanId = scopeMembers.find { member -> Role.Headman in member.roles }?.user?.id,
            students = students
        )
    }.stateInResource(componentScope)


//    val memberAction = MutableStateFlow<Pair<List<StudentAction>, UUID>?>(null)

    val memberActions = MutableStateFlow<Pair<List<MemberAction>, UUID>?>(null)

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

    fun onMemberEditClick(memberId: UUID) {
        onMemberEdit(memberId)
    }


    fun onMemberRemoveClick(memberId: UUID) {
        componentScope.launch {
            if (confirmDialogInteractor.confirmRequest(
                    ConfirmState(
                        uiTextOf("Удалить члена группы?"),
                        uiTextOf("Удалятся все данные, связанные с данным пользователем в группе")
                    )
                )
            ) {
                removeMemberFromScopeUseCase(memberId, studyGroupId)
            }
        }
    }

    fun onMemberSetHeadmanClick(memberId: UUID) {
        componentScope.launch {
            assignUserRoleInScopeUseCase(
                memberId,
                Role.Headman.id,
                studyGroupId
            )
        }
    }

    fun onMemberRemoveHeadmanClick(memberId: UUID) {
        componentScope.launch {
            removeUserRoleFromScopeUseCase(
                memberId,
                Role.Headman.id,
                studyGroupId
            )
        }
    }


    fun onMemberActionsExpand(memberId: UUID) {
        members.value.onSuccess { members ->
            memberActions.update {
                buildList {
                    if (members.students.any { it.id == memberId }) {
                        add(
                            if (members.headmanId != memberId) MemberAction.SET_HEADMAN
                            else MemberAction.REMOVE_HEADMAN
                        )
                    }
                    add(MemberAction.EDIT)
                    add(MemberAction.REMOVE)
                } to memberId
            }
        }
    }

    fun onDismissActions() {
        memberActions.value = null
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

    enum class MemberAction { EDIT, REMOVE, SET_HEADMAN, REMOVE_HEADMAN }

    enum class StudentAction(
        override val title: String,
        override val iconName: String? = null,
    ) : MenuAction {
        SetHeadman("Назначить старостой"),
        RemoveHeadman("Лишить прав старосты"),
//        Edit("Редактировать")
    }
}
