package com.denchic45.studiversity.ui.settings

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.api.account.model.UpdatePasswordRequest
import com.denchic45.studiversity.domain.resource.onSuccess
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
import com.github.michaelbull.result.onSuccess
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject


@Inject
class UpdatePasswordComponent(
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    @Assisted
   private val onFinish: () -> Unit,
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
                            state.newPasswordMessage = getIfNot(it) { "Минимальная длина пароля - 6 символов" }
                        },
                    Condition(String::containsLatinChars)
                        .observable {
                            state.newPasswordMessage = getIfNot(it) { "Пароль должен содержать латинские символы" }
                        },
                    Condition(String::containsUpperAndLowerCaseChars)
                        .observable {
                            state.newPasswordMessage =
                                getIfNot(it) { "Пароль должен содержать символы в верхнем и нижнем регистрах" }
                        },
                    Condition(String::containsDigits)
                        .observable { state.newPasswordMessage = getIfNot(it) { "Пароль должен содержать цифры" } }
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

                }
        }
    }

    fun onDismissRequest() {
        onFinish()
    }
}

@Stable
class UpdatePasswordState {
    var oldPassword: String by mutableStateOf("")
    var newPassword: String by mutableStateOf("")
    var repeatedNewPassword: String by mutableStateOf("")
    var oldPasswordMessage: String? by mutableStateOf(null)
    var newPasswordMessage: String? by mutableStateOf(null)
    var repeatedNewPasswordMessage: String? by mutableStateOf(null)
}
