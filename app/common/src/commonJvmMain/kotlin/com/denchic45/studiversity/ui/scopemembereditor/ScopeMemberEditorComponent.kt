package com.denchic45.studiversity.ui.scopemembereditor

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.ChildOverlay
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.overlay.dismiss
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.domain.ifSuccess
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.domain.resourceOf
import com.denchic45.studiversity.domain.usecase.AddMemberToScopeManuallyUseCase
import com.denchic45.studiversity.domain.usecase.AssignRolesToUserInScopeUseCase
import com.denchic45.studiversity.domain.usecase.FindMemberByScopeIdAndMemberIdUseCase
import com.denchic45.studiversity.ui.model.UserItem
import com.denchic45.studiversity.ui.model.toUserItem
import com.denchic45.studiversity.ui.search.UserChooserComponent
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.role.model.Role
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class ScopeMemberEditorComponent(
    userChooserComponent: (onSelect: (UserItem) -> Unit, ComponentContext) -> UserChooserComponent,
    private val findMemberByScopeIdAndMemberIdUseCase: FindMemberByScopeIdAndMemberIdUseCase,
    private val assignRolesToUserInScopeUseCase: AssignRolesToUserInScopeUseCase,
    private val addMemberToScopeManuallyUseCase: AddMemberToScopeManuallyUseCase,
    @Assisted
    private val availableRoles: List<Role>,
    @Assisted
    private val scopeId: UUID,
    @Assisted
    private val memberId: UUID?,
    @Assisted
    val onFinish: () -> Unit,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    private val overlayNavigation = OverlayNavigation<Config>()

    val childOverlay: Value<ChildOverlay<Config, Child>> = childOverlay(
        source = overlayNavigation,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                Config.UserChooser -> Child.UserChooser(
                    userChooserComponent(
                        ::onSelectUser,
                        context
                    )
                )
            }
        }
    )

    val state = MutableStateFlow(EditableMemberState(availableRoles))
    val stateResource = memberId?.let {
        flow {
            val resource = findMemberByScopeIdAndMemberIdUseCase(scopeId, memberId)
            resource.ifSuccess {
                state.value = EditableMemberState(availableRoles, it.user.toUserItem(), it.roles)
                emitAll(state.map(::resourceOf))
            } ?: flowOf(resource)
        }
    } ?: state.map(::resourceOf)

    val isNew = memberId == null

    val allowSave = state.map {
        it.availableRoles.isNotEmpty() && it.user != null
    }.stateIn(componentScope, SharingStarted.Lazily, false)

    private fun onSelectUser(userItem: UserItem) {
        overlayNavigation.dismiss()
        state.update { it.copy(user = userItem) }
    }

    fun onRoleClick(role: Role) {
        state.update { state ->
            state.copy(
                assignedRoles = if (role in this.state.value.assignedRoles)
                    state.assignedRoles - role
                else
                    state.assignedRoles + role
            )
        }
    }

    fun onClose() {
        onFinish()
    }

    fun onAddUserClick() {
        overlayNavigation.activate(Config.UserChooser)
    }

    fun onSaveClick() {
        componentScope.launch {
            val roleIds = state.value.assignedRoles.map { it.id }
            val result = memberId?.let {
                assignRolesToUserInScopeUseCase(memberId, scopeId, roleIds)
            } ?: run {
                addMemberToScopeManuallyUseCase(state.value.user!!.id, scopeId, roleIds)
            }

            result.onSuccess {
                withContext(Dispatchers.Main) {
                    onFinish()
                }
            }
        }
    }

    data class EditableMemberState(
        val availableRoles: List<Role>,
        val user: UserItem? = null,
        val assignedRoles: List<Role> = emptyList()
    )

    @Parcelize
    sealed interface Config : Parcelable {
        object UserChooser : Config
    }

    sealed interface Child {
        class UserChooser(val component: UserChooserComponent) : Child
    }
}