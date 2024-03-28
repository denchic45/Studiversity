package com.denchic45.studiversity.ui.settings

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.api.account.AccountErrors
import com.denchic45.studiversity.api.account.model.UpdatePasswordRequest
import com.denchic45.studiversity.domain.resource.*
import com.denchic45.studiversity.domain.usecase.UpdatePasswordUseCase
import com.denchic45.studiversity.uivalidator.condition.Condition
import com.denchic45.studiversity.uivalidator.condition.observable
import com.denchic45.studiversity.uivalidator.getIfNot
import com.denchic45.studiversity.uivalidator.validator.CompositeValidator
import com.denchic45.studiversity.uivalidator.validator.ValueValidator
import com.denchic45.studiversity.uivalidator.validator.observable
import com.denchic45.studiversity.util.componentScope
import com.denchic45.studiversity.util.containsDigits
import com.denchic45.studiversity.util.containsLatinChars
import com.denchic45.studiversity.util.containsUpperAndLowerCaseChars
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject


@Inject
class UpdatePasswordComponent(
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    @Assisted
    private val onFinish: (success: Boolean) -> Unit,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    private val componentScope = componentScope()
    val state = UpdatePasswordState()

    val validator = CompositeValidator(
        listOf(
            ValueValidator(
                value = state::oldPassword,
                conditions = listOf(Condition(String::isNotEmpty))
            ).observable { state.oldPasswordMessage = getIfNot(it) { "Введите старый пароль" } },
            ValueValidator(
                value = state::newPassword,
                conditions = listOf(
                    Condition(String::isNotEmpty).observable { getIfNot(it) { "Введите новый пароль" } },
                    Condition<String> { it.length >= 6 }
                        .observable {
                            state.newPasswordMessage = getIfNot(it) { PASSWORD_MUST_CONTAIN_AT_LEAST_6_CHARACTERS }
                        },
                    Condition(String::containsLatinChars)
                        .observable {
                            state.newPasswordMessage = getIfNot(it) { PASSWORD_MUST_CONTAIN_LATIN_CHARS }
                        },
                    Condition(String::containsUpperAndLowerCaseChars)
                        .observable {
                            state.newPasswordMessage = getIfNot(it) { PASSWORD_MUST_CONTAIN_UPPER_AND_LOWER_CASE_CHARS }
                        },
                    Condition(String::containsDigits)
                        .observable {
                            state.newPasswordMessage = getIfNot(it) { PASSWORD_MUST_CONTAIN_DIGITS }
                        }
                )
            ),
            ValueValidator(
                value = state::repeatedNewPassword,
                conditions = listOf(Condition { it == state.newPassword })
            ).observable { state.repeatedNewPasswordMessage = getIfNot(it) { "Пароли не совпадают" } }
        )
    )

    fun onOldPasswordChange(text: String) {
        state.oldPassword = text
    }

    fun onNewPasswordChange(text: String) {
        state.newPassword = text
    }

    fun onRepeatedNewPasswordChange(text: String) {
        state.repeatedNewPassword = text
    }

    fun onSave() {
        if (!validator.validate()) return

        componentScope.launch {
            updatePasswordUseCase(UpdatePasswordRequest(state.oldPassword, state.newPassword))
                .onSuccess {
                    onFinish()
                }.onFailure {
                    state.resultMessage = when (it) {
                        is ClientError -> when (it.response.error.reason) {
                            AccountErrors.INVALID_OLD_PASSWORD -> "Старый пароль неверный"
                            AccountErrors.OLD_PASSWORD_ENTERED -> "Новый пароль должен отличаться от старого"

                            AccountErrors.PASSWORD_MUST_CONTAIN_AT_LEAST_6_CHARACTERS -> PASSWORD_MUST_CONTAIN_AT_LEAST_6_CHARACTERS
                            AccountErrors.PASSWORD_MUST_CONTAIN_LATIN_CHARS -> PASSWORD_MUST_CONTAIN_LATIN_CHARS
                            AccountErrors.PASSWORD_MUST_CONTAIN_UPPER_AND_LOWER_CASE_CHARS -> PASSWORD_MUST_CONTAIN_UPPER_AND_LOWER_CASE_CHARS
                            AccountErrors.PASSWORD_MUST_CONTAIN_DIGITS -> PASSWORD_MUST_CONTAIN_DIGITS

                            else -> "Неизвестная ошибка"
                        }

                        ServerError -> "Ошибка на стороне сервера"
                        else -> "Неизвестная ошибка"
                    }
                }
        }
    }

    fun onDismissRequest() {
        onFinish()
    }

    companion object ErrorMessages {
        const val PASSWORD_MUST_CONTAIN_AT_LEAST_6_CHARACTERS = "Минимальная длина пароля - 6 символов"
        const val PASSWORD_MUST_CONTAIN_UPPER_AND_LOWER_CASE_CHARS =
            "Пароль должен содержать символы в верхнем и нижнем регистрах"
        const val PASSWORD_MUST_CONTAIN_LATIN_CHARS = "Пароль должен содержать латинские символы"
        const val PASSWORD_MUST_CONTAIN_DIGITS = "Пароль должен содержать цифры"
    }
}

@Stable
class UpdatePasswordState {
    var oldPassword by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var repeatedNewPassword by mutableStateOf("")
    var oldPasswordMessage: String? by mutableStateOf(null)
    var newPasswordMessage: String? by mutableStateOf(null)
    var repeatedNewPasswordMessage: String? by mutableStateOf(null)

    val resultMessage by mutableStateOf("")
}
