package com.denchic45.kts.ui.usereditor

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.denchic45.kts.domain.onFailure
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.usecase.AddUserUseCase
import com.denchic45.kts.domain.usecase.ObserveUserUseCase
import com.denchic45.kts.domain.usecase.RemoveUserUseCase
import com.denchic45.kts.ui.navigation.ConfirmConfig
import com.denchic45.kts.ui.navigation.OverlayConfig
import com.denchic45.kts.ui.navigation.UserEditorConfig
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class UserEditorComponent(
    observeUserUseCase: ObserveUserUseCase,
    addUserUseCase: AddUserUseCase,
    private val removeUserUseCase: RemoveUserUseCase,
    private val overlayNavigation: OverlayNavigation<OverlayConfig>,
    componentContext: ComponentContext,
    val _onFinish: () -> Unit,
    config: UserEditorConfig,
) : ComponentContext by componentContext,
    UserEditorUILogicDelegate(
        observeUserUseCase,
        addUserUseCase,
        config.userId, componentContext
    ) {

    private val componentScope = componentScope()

    override fun onDeleteUser() {
        overlayNavigation.activate(ConfirmConfig("Удалить студента", "Вы уверены?") {
            componentScope.launch {
                removeUserUseCase(userId!!)
                    .onSuccess { onFinish() }
                    .onFailure {
                        // TODO: check errors
                    }
            }
        })
    }

    override fun onFinish() {
      _onFinish()
    }
}