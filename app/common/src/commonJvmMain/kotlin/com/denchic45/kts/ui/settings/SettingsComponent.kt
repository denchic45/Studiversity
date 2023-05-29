package com.denchic45.kts.ui.settings

import com.arkivanov.decompose.ComponentContext
import me.tatarka.inject.annotations.Inject

@Inject
class SettingsComponent(
    private val componentContext: ComponentContext
) : ComponentContext by componentContext {
}