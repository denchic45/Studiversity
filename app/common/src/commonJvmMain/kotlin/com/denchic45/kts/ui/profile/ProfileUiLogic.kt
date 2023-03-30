package com.denchic45.kts.ui.profile

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.filterSuccess
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.usecase.ObserveUserUseCase
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Assisted
import java.util.*


abstract class ProfileUiLogic(
    observeUserUseCase: ObserveUserUseCase,
    userId: UUID,
    componentContext: ComponentContext,
)  {

    private val componentScope = componentContext.componentScope()

    private val userFlow = observeUserUseCase(userId)

    val profileViewState: StateFlow<Resource<ProfileViewState>> =
        userFlow.filterSuccess().mapResource {
            it.toProfileViewState()
        }.stateIn(componentScope, SharingStarted.Lazily, Resource.Loading)

    abstract fun onAvatarClick()
}