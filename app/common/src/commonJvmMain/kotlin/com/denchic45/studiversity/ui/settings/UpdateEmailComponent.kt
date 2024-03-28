package com.denchic45.studiversity.ui.settings

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.api.account.AccountErrors
import com.denchic45.studiversity.domain.resource.ClientError
import com.denchic45.studiversity.domain.resource.onFailure
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.usecase.UpdateEmailUseCase
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject


@Inject
class UpdateEmailComponent(
    private val updateEmailUseCase: UpdateEmailUseCase,
    @Assisted
    private val onFinish: (success: Boolean) -> Unit,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    val state = UpdateEmailState()

    fun onEmailChange(text: String) {
        state.email = text
    }

    fun onUpdateEmailClick() {
        componentScope.launch {
            updateEmailUseCase(state.email)
                .onSuccess {
                    onFinish(true)
                }
                .onFailure {
                    state.emailMessage = when (val error = it) {
                        is ClientError -> when (error.response.error.reason) {
                            AccountErrors.INVALID_EMAIL -> "Введена некорректная почта"
                            AccountErrors.OLD_EMAIL_ENTERED -> "Введите новую почту"
                            else -> "Неизвестная ошибка"
                        }

                        else -> "Неизвестная ошибка"
                    }
                }
        }
    }

    fun onDismissRequest() {
        onFinish(false)
    }
}

@Stable
class UpdateEmailState {
    var email by mutableStateOf("")
    var emailMessage: String? by mutableStateOf(null)
}