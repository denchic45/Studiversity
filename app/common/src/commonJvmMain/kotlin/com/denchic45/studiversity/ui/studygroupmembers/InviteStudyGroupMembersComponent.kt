package com.denchic45.studiversity.ui.studygroupmembers

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.api.member.CreateMemberRequest
import com.denchic45.studiversity.api.role.model.Role
import com.denchic45.studiversity.api.user.model.UserResponse
import com.denchic45.studiversity.domain.model.UserItem
import com.denchic45.studiversity.domain.model.toUserItem
import com.denchic45.studiversity.domain.resource.mapResource
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.resource.suspendBindResources
import com.denchic45.studiversity.domain.usecase.AddStudyGroupMemberUseCase
import com.denchic45.studiversity.domain.usecase.FindAssignableRolesByUserAndScopeUseCase
import com.denchic45.studiversity.domain.usecase.FindUserByContainsNameUseCase
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class InviteStudyGroupMembersComponent(
    findAssignableRolesByUserAndScopeUseCase: FindAssignableRolesByUserAndScopeUseCase,
    private val findUserByContainsNameUseCase: FindUserByContainsNameUseCase,
    private val addStudyGroupMemberUseCase: AddStudyGroupMemberUseCase,
    @Assisted
    private val onFinish: (Boolean) -> Unit,
    @Assisted
    private val studyGroupId: UUID,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    val state = InvitedMembersState()
    private val queryText = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    private val foundMembers = queryText.flatMapLatest(findUserByContainsNameUseCase::invoke)
        .mapResource { it.map(UserResponse::toUserItem) }.stateInResource(componentScope)

    private val assignableSystemRoles = findAssignableRolesByUserAndScopeUseCase(null, studyGroupId)
        .stateInResource(componentScope)

    init {
        assignableSystemRoles.onEach {
            it.onSuccess { assignableRoles -> state.assignableRoles = assignableRoles }
        }.launchIn(componentScope)

        foundMembers.onEach {
            it.onSuccess { members -> state.foundMembers = members }
        }.launchIn(componentScope)
    }

    fun onQueryChange(text: String) {
        state.queryText = text
        queryText.value = text
    }

    fun onMemberSelect(item: UserItem) {
        state.members.add(item)
    }

    fun onMemberRemove(item: UserItem) {
        state.members.remove(item)
    }

    fun onRoleChange(role: Role) {
        state.role = role
    }

    fun onSave() {
        componentScope.launch {
            suspendBindResources {
                val roleId = state.role!!.id
                state.members.forEach {
                    addStudyGroupMemberUseCase(studyGroupId, CreateMemberRequest(it.id, listOf(roleId))).bind()
                }
            }.onSuccess { withContext(Dispatchers.Main) { onFinish(true) } }
        }
    }

    fun onDismissRequest() {
        onFinish(false)
    }
}

@Stable
class InvitedMembersState {
    var queryText by mutableStateOf("")
    var foundMembers by mutableStateOf(emptyList<UserItem>())
    var assignableRoles by mutableStateOf(listOf<Role>())

    val members = mutableStateListOf<UserItem>()
    var role by mutableStateOf<Role?>(null)

    val allowSave: Boolean
        get() = members.isNotEmpty() && role != null
}
