package com.denchic45.studiversity.ui.account

import com.arkivanov.decompose.ComponentContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject


@Inject
class AccountComponent(
    @Assisted
    private val componentContext: ComponentContext
) : ComponentContext by componentContext