package com.denchic45.studiversity.ui.settings

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.Field
import com.denchic45.studiversity.FieldEditor
import com.denchic45.studiversity.data.service.AccountService
import com.denchic45.studiversity.getOptProperty
import com.denchic45.studiversity.uivalidator.condition.Condition
import com.denchic45.studiversity.uivalidator.getIfNot
import com.denchic45.studiversity.uivalidator.validator.CompositeValidator
import com.denchic45.studiversity.uivalidator.validator.ValueValidator
import com.denchic45.studiversity.uivalidator.validator.observable
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.account.model.UpdateAccountPersonalRequest
import com.denchic45.stuiversity.api.user.model.Gender
import com.github.michaelbull.result.onSuccess
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject


@Inject
class PersonalityComponent(
    private val accountService: AccountService,
    @Assisted
    private val componentContext: ComponentContext
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    val state = State()

    private val fieldEditor = FieldEditor(
        mapOf(
            "firstName" to Field(state::firstName),
            "surname" to Field(state::surname),
            "patronymic" to Field(state::patronymic),
            "gender" to Field(state::gender)
        )
    )

    private val validator = CompositeValidator(
        validators = listOf(
            ValueValidator(
                value = state::firstName,
                conditions = listOf(Condition { it.trim().isNotEmpty() })
            ).observable { isValid ->
                state.firstNameMessage = getIfNot(isValid) { "Имя обязательно" }
            },
            ValueValidator(
                value = state::surname,
                conditions = listOf(Condition { it.trim().isNotEmpty() })
            ).observable { isValid ->
                state.surnameMessage = getIfNot(isValid) { "Фамилия обязательна" }
            }
        )
    )

    fun allowSave(): Boolean {
        return fieldEditor.hasChanges() && validator.validate()
    }

    init {
        val response = accountService.findMe()
        state.apply {
            firstName = response.firstName
            surname = response.surname
            patronymic = response.patronymic.orEmpty()
            gender = response.gender
            avatarUrl = response.avatarUrl
        }
        fieldEditor.updateOldValues()
    }

    fun onFirstNameChange(text: String) {
        state.firstName = text
    }

    fun onSurnameChange(text: String) {
        state.surname = text
    }

    fun onPatronymicChange(text: String) {
        state.patronymic = text
    }

    fun onGenderSelect(gender: Gender) {
        state.gender = gender
    }

    fun onSaveClick() {
        componentScope.launch {
            accountService.updatePersonal(
                UpdateAccountPersonalRequest(
                    firstName = fieldEditor.getOptProperty("firstName"),
                    surname = fieldEditor.getOptProperty("surname"),
                    patronymic = fieldEditor.getOptProperty("patronymic"),
                    gender = fieldEditor.getOptProperty("gender")
                )
            ).onSuccess {
                fieldEditor.updateOldValues()
            }
        }
    }

    @Stable
    class State {
        var firstName by mutableStateOf("")
        var surname by mutableStateOf("")
        var patronymic by mutableStateOf("")
        var gender by mutableStateOf(Gender.UNKNOWN)
        var avatarUrl by mutableStateOf("")

        val fullName: String
            get() = "$firstName $surname".trim()

        var firstNameMessage by mutableStateOf<String?>(null)
        var surnameMessage by mutableStateOf<String?>(null)

    }
}