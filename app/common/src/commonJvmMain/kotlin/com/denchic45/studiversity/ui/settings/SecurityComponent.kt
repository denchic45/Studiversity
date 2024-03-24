package com.denchic45.studiversity.ui.settings

import com.arkivanov.decompose.ComponentContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject


@Inject
class SecurityComponent(
    @Assisted
    private val componentContext: ComponentContext
) : ComponentContext by componentContext