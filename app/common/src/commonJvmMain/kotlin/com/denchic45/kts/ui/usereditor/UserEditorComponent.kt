package com.denchic45.kts.ui.usereditor

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.Field
import com.denchic45.kts.FieldEditor
import com.denchic45.kts.domain.onFailure
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.usecase.AddUserUseCase
import com.denchic45.kts.ui.model.MenuAction
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.user.model.CreateUserRequest
import com.denchic45.stuiversity.api.user.model.Gender
import com.denchic45.uivalidator.experimental2.Operator
import com.denchic45.uivalidator.experimental2.condition.Condition
import com.denchic45.uivalidator.experimental2.condition.observable
import com.denchic45.uivalidator.experimental2.getIfNot
import com.denchic45.uivalidator.experimental2.validator.CompositeValidator
import com.denchic45.uivalidator.experimental2.validator.ValueValidator
import com.denchic45.uivalidator.isEmail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class UserEditorComponent(
    private val addUserUseCase: AddUserUseCase,
    @Assisted
    val onFinish: () -> Unit,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    val allowSave = MutableStateFlow(false)

    @Stable
    class CreatableUserState(val genders: List<GenderAction>) {
        var firstName by mutableStateOf("")
        var surname by mutableStateOf("")
        var patronymic by mutableStateOf("")
        var gender by mutableStateOf(GenderAction.Undefined)
        var email by mutableStateOf("")

        var firstNameMessage: String? by mutableStateOf(null)
        var surnameMessage: String? by mutableStateOf(null)
        var genderMessage: String? by mutableStateOf(null)
        var emailMessage: String? by mutableStateOf(null)
    }

    private val genders: List<GenderAction> = listOf(
        GenderAction.Male,
        GenderAction.Female,
        GenderAction.Undefined
    )
    val state = CreatableUserState(genders)

    private val fieldEditor = FieldEditor(
        mapOf(
            "firstName" to Field(state::firstName),
            "surname" to Field(state::surname),
            "patronymic" to Field(state::patronymic),
            "gender" to Field(state::gender),
            "email" to Field(state::email)
        )
    )

    private val emailValidator = ValueValidator(
        value = state::email,
        conditions = listOf(
            Condition(String::isNotEmpty).observable { isValid ->
                state.emailMessage = getIfNot(isValid) { "Почта обязательна" }
            },
            Condition(String::isEmail).observable { isValid ->
                state.emailMessage = getIfNot(isValid) { "Некоректная почта" }
            }
        ),
        operator = Operator.all()
    )

    private val validator = CompositeValidator(
        listOf(
            ValueValidator(
                value = state::firstName,
                conditions = listOf(
                    Condition(String::isNotEmpty)
                        .observable { isValid ->
                            state.firstNameMessage = getIfNot(isValid) { "Имя обязательно" }
                        })
            ),
            ValueValidator(
                value = state::surname,
                conditions = listOf(Condition(String::isNotEmpty).observable { isValid ->
                    state.surnameMessage = getIfNot(isValid) { "Фамилия обязательно" }
                })
            ),
            ValueValidator(
                value = state::gender,
                conditions = listOf(Condition<GenderAction> { it != GenderAction.Undefined }.observable { isValid ->
                    state.genderMessage = getIfNot(isValid) { "Пол обязателен" }
                })
            ),
            emailValidator
        )
    )

    fun onSaveClick() {
        validator.validate()
        saveChanges()
    }

    fun onFirstNameType(firstName: String) {
        state.firstName = firstName
        updateAllowSave()
    }

    fun onSurnameType(surname: String) {
        state.surname = surname
        updateAllowSave()
    }

    fun onPatronymicType(patronymic: String) {
        state.patronymic = patronymic
    }

    fun onEmailType(email: String) {
        state.email = email
        updateAllowSave()
    }

    fun onGenderSelect(gender: GenderAction) {
        state.gender = gender
        updateAllowSave()
    }

    private fun updateAllowSave() {
        allowSave.update { fieldEditor.hasChanges() }
    }

    private fun saveChanges() {
        componentScope.launch {
            validator.onValid {
                addUserUseCase(
                    CreateUserRequest(
                        state.firstName,
                        state.surname,
                        state.patronymic,
                        state.email,
                        when (state.gender) {
                            GenderAction.Undefined -> Gender.UNKNOWN
                            GenderAction.Male -> Gender.MALE
                            GenderAction.Female -> Gender.FEMALE
                        }
                    )
                ).onSuccess {
                    withContext(Dispatchers.Main.immediate) {
                        onFinish()
                    }
                }
                    .onFailure { }
            }
        }
    }

    enum class GenderAction(override val title: String, override val iconName: String? = null) :
        MenuAction {
        Undefined("Не выбран"),
        Male("Мужской"),
        Female("Женский")
    }
}