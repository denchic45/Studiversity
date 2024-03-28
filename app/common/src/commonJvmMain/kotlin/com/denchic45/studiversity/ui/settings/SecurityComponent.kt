package com.denchic45.studiversity.ui.settings

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.usecase.FindAccountEmailUseCase
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject


@Inject
class SecurityComponent(
    private val findAccountEmailUseCase: FindAccountEmailUseCase,
    private val updateEmailComponent: ((Boolean) -> Unit, ComponentContext) -> UpdateEmailComponent,
    private val updatePasswordComponent: ((Boolean) -> Unit, ComponentContext) -> UpdatePasswordComponent,
    @Assisted
    private val componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val componentScope = componentScope()
    private val slotNavigation = SlotNavigation<Config>()
    val childSlot = childSlot(
        source = slotNavigation,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                Config.UpdateEmail -> Child.UpdateEmail(updateEmailComponent({ success ->
                    slotNavigation.dismiss()
                    if (success) fetchEmail()
                }, context))

                Config.UpdatePassword -> Child.UpdatePassword(
                    updatePasswordComponent(
                        {
                            slotNavigation.dismiss()
                        },
                        context
                    )
                )
            }
        }
    )

    val state = AccountState()

    init {
        fetchEmail()
    }

    private fun fetchEmail() {
        componentScope.launch {
            findAccountEmailUseCase().onSuccess {
                state.email = it.currentEmail
                state.needToConfirm = it.needToConfirm
            }
        }
    }

    fun onUpdateEmailClick() = slotNavigation.activate(Config.UpdateEmail)

    fun onUpdatePasswordClick() = slotNavigation.activate(Config.UpdatePassword)

    @Parcelize
    sealed interface Config : Parcelable {
        data object UpdateEmail : Config {
            private fun readResolve(): Any = UpdateEmail
        }


        data object UpdatePassword : Config {
            private fun readResolve(): Any = UpdateEmail
        }
    }


    sealed interface Child {

        class UpdateEmail(val component: UpdateEmailComponent) : Child

        class UpdatePassword(val component: UpdatePasswordComponent) : Child
    }
}


@Stable
class AccountState {
    var email by mutableStateOf("")
    var needToConfirm by mutableStateOf(false)
}