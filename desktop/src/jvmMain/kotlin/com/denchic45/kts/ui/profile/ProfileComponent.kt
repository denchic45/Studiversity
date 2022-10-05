package com.denchic45.kts.ui.profile

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.usecase.ObserveGroupNameUseCase
import com.denchic45.kts.domain.usecase.ObserveUserUseCase
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject

@Inject
class ProfileComponent(
    userId: String,
    observeUserUseCase: ObserveUserUseCase,
    observeGroupNameUseCase: ObserveGroupNameUseCase,
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    private val userFlow = observeUserUseCase(userId)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val groupNameFlow = userFlow.filterNotNull().flatMapLatest {
        it.groupId?.let { groupId -> observeGroupNameUseCase(groupId) } ?: flowOf(null)
    }

    val profileViewState: StateFlow<ProfileViewState?> = combine(
        userFlow.filterNotNull(), groupNameFlow
    ) { user, groupName -> user.toProfileViewState(groupName) }.stateIn(
        componentScope, SharingStarted.Lazily, null
    )
}