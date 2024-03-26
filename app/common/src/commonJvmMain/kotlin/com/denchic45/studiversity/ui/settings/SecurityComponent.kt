package com.denchic45.studiversity.ui.settings

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject


@Inject
class SecurityComponent(
    @Assisted
    private val componentContext: ComponentContext
) : ComponentContext by componentContext {
val state = AccountState()

    fun on
}

@Stable
class AccountState {
    var email:String by mutableStateOf("")
}