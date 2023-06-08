package com.denchic45.kts.ui.auth

import com.arkivanov.decompose.ComponentContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class RegistrationComponent(
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
}