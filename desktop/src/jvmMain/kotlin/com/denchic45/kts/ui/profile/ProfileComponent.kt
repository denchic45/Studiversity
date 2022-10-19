package com.denchic45.kts.ui.profile

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigator
import com.arkivanov.decompose.router.stack.push
import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.domain.model.GroupHeader
import com.denchic45.kts.domain.model.User
import com.denchic45.kts.domain.usecase.ObserveGroupInfoUseCase
import com.denchic45.kts.domain.usecase.ObserveGroupNameByCuratorUseCase
import com.denchic45.kts.domain.usecase.ObserveUserUseCase
import com.denchic45.kts.ui.navigation.GroupConfig
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class ProfileComponent(
    observeUserUseCase: ObserveUserUseCase,
    private val observeGroupInfoUseCase: ObserveGroupInfoUseCase,
    private val observeGroupNameByCuratorUseCase: ObserveGroupNameByCuratorUseCase,
    componentContext: ComponentContext,
    private val navigator: StackNavigator<in GroupConfig.Group>,
    private val groupClickable: Boolean,
    userId: String,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    private val userFlow: Flow<User?> = observeUserUseCase(userId)

    @OptIn(FlowPreview::class)
    private val groupInfoFlow: StateFlow<GroupHeader?> =
        userFlow.filterNotNull().flatMapMerge { user ->
            when (user.role) {
                UserRole.STUDENT -> observeGroupInfoUseCase(user.groupId!!)
                UserRole.TEACHER, UserRole.HEAD_TEACHER -> observeGroupNameByCuratorUseCase(user.id)
            }
        }.stateIn(componentScope, SharingStarted.Lazily, null)

    val profileViewState: StateFlow<ProfileViewState?> =
        combine(userFlow.filterNotNull(), groupInfoFlow.map { it?.name }) { user, group ->
            user.toProfileViewState(groupName = group.let {
                when (user.role) {
                    UserRole.STUDENT -> "Участник группы: $group"
                    UserRole.TEACHER,
                    UserRole.HEAD_TEACHER,
                    -> "Куратор группы: $group"
                }
            }, groupClickable)
        }.stateIn(componentScope, SharingStarted.Lazily, null)

    private val observe = observeUserUseCase(userId)

    val photoUrl = observe.map { it?.photoUrl }
        .stateIn(componentScope, SharingStarted.Lazily, null)

    fun onGroupClick() {
        navigator.push(GroupConfig.Group(groupInfoFlow.value!!.id))
    }
}