package com.denchic45.kts.ui.profile

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigator
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.filterSuccess
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.usecase.FindStudyGroupByIdUseCase
import com.denchic45.kts.domain.usecase.ObserveUserUseCase
import com.denchic45.kts.ui.navigation.GroupConfig
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.util.toUUID
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class ProfileComponent(
    observeUserUseCase: ObserveUserUseCase,
    componentContext: ComponentContext,
    private val userId: UUID,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    private val userFlow = observeUserUseCase(userId)

    val profileViewState: StateFlow<Resource<ProfileViewState>> =
        userFlow.filterSuccess().mapResource {
            it.toProfileViewState()
        }.stateIn(componentScope, SharingStarted.Lazily, Resource.Loading)
}