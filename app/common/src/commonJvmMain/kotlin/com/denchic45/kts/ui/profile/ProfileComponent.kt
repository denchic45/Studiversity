package com.denchic45.kts.ui.profile

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.usecase.ObserveUserUseCase
import com.denchic45.kts.util.componentScope
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class ProfileComponent(
    observeUserUseCase: ObserveUserUseCase,
    @Assisted
    userId: UUID,
    @Assisted
    componentContext: ComponentContext,
) : ProfileUiLogic(observeUserUseCase, userId, componentContext),
    ComponentContext by componentContext {

    private val componentScope = componentScope()

    override fun onAvatarClick() {
        TODO("Not yet implemented")
    }
}