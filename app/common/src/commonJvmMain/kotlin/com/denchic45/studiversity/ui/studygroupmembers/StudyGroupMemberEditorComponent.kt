package com.denchic45.studiversity.ui.studygroupmembers

import androidx.compose.runtime.Stable
import androidx.compose.runtime.toMutableStateList
import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.api.role.model.Role
import com.denchic45.studiversity.domain.model.UserItem
import com.denchic45.studiversity.domain.resource.bindResources
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.resource.success
import com.denchic45.studiversity.domain.usecase.AssignRolesToUserInScopeUseCase
import com.denchic45.studiversity.domain.usecase.FindAssignableRolesByUserAndScopeUseCase
import com.denchic45.studiversity.domain.usecase.FindAssignedUserRolesInScopeUseCase
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class StudyGroupMemberEditorComponent(
    findAssignableRolesByUserAndScopeUseCase: FindAssignableRolesByUserAndScopeUseCase,
    findAssignedUserRolesInScopeUseCase: FindAssignedUserRolesInScopeUseCase,
    private val assignRolesToUserInScopeUseCase: AssignRolesToUserInScopeUseCase,
    @Assisted
    private val onFinish: (Boolean) -> Unit,
    @Assisted
    private val studyGroupId: UUID,
    @Assisted
    private val userItem: UserItem,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()
    val stateResource = combine(
        findAssignableRolesByUserAndScopeUseCase(null, studyGroupId),
        findAssignedUserRolesInScopeUseCase(userItem.id, studyGroupId)
    ) { assignableRoles, assignedRoles ->
        bindResources {
            EditingMemberState(userItem, assignableRoles.bind(), assignedRoles.bind().roles)
        }
    }.stateInResource(componentScope)

    private val state by lazy {
        stateResource.value.success().value
    }

    fun onRoleClick(role: Role) = state.apply {
        if (role in assignedRoles) assignedRoles.remove(role)
        else assignedRoles.add(role)
    }

    fun onSave() {
        componentScope.launch {
            assignRolesToUserInScopeUseCase(
                userItem.id,
                studyGroupId,
                state.assignedRoles.map(Role::id)
            ).onSuccess {
                withContext(Dispatchers.Main) {
                    onFinish(true)
                }
            }
        }
    }

    fun onDismissRequest() = onFinish(false)
}

@Stable
class EditingMemberState(
    val member: UserItem,
    val assignableRoles: List<Role>,
    assignedRoles: List<Role>
) {
    val assignedRoles = assignedRoles.toMutableStateList()

    val allowSave: Boolean get() = assignedRoles.isNotEmpty()
}