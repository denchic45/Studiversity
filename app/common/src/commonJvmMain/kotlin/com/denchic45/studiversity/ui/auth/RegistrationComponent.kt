package com.denchic45.studiversity.ui.auth

import com.arkivanov.decompose.ComponentContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class RegistrationComponent(
    @Assisted
    private val onSuccess: () -> Unit,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
}