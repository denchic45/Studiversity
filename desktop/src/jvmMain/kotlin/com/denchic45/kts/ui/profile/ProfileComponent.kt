package com.denchic45.kts.ui.profile

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.domain.usecase.ObserveGroupNameByCuratorUseCase
import com.denchic45.kts.domain.usecase.ObserveGroupNameUseCase
import com.denchic45.kts.domain.usecase.ObserveUserUseCase
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject

@Inject
class ProfileComponent(
    observeUserUseCase: ObserveUserUseCase,
    private val observeGroupNameUseCase: ObserveGroupNameUseCase,
    private val observeGroupNameByCuratorUseCase: ObserveGroupNameByCuratorUseCase,
    componentContext: ComponentContext,
    userId: String,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    @OptIn(FlowPreview::class)
    val profileViewState: StateFlow<ProfileViewState?> =
        observeUserUseCase(userId).filterNotNull().flatMapMerge { user ->
            when (user.role) {
                UserRole.STUDENT -> {
                    observeGroupNameUseCase(user.groupId!!).map { groupName ->
                        user.toProfileViewState("Участник группы: $groupName")
                    }
                }
                UserRole.TEACHER, UserRole.HEAD_TEACHER -> {
                    observeGroupNameByCuratorUseCase(user.id).map { groupName ->
                        user.toProfileViewState("Куратор группы: $groupName")
                    }
                }
            }
        }.stateIn(componentScope, SharingStarted.Lazily, null)
}