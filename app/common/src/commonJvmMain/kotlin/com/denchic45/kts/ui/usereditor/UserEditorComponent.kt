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
import com.denchic45.uivalidator.experimental2.Operator
import com.denchic45.uivalidator.experimental2.condition.Condition
import com.denchic45.uivalidator.experimental2.condition.observable
import com.denchic45.uivalidator.experimental2.getIfNot
import com.denchic45.uivalidator.experimental2.validator.CompositeValidator
import com.denchic45.uivalidator.experimental2.validator.ValueValidator
import com.denchic45.uivalidator.isEmail
import kotlinx.coroutines.launch
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

    @Stable
    class CreatableUserState(val genders: List<GenderAction>) {
        var firstName by mutableStateOf("")
        var surname by mutableStateOf("")
        var patronymic by mutableStateOf("")
        var gender by mutableStateOf(GenderAction.Undefined)
        var email by mutableStateOf("")

        var firstNameMessage: String? by mutableStateOf(null)
        var surnameMessage: String? by mutableStateOf(null)
        var emailMessage: String? by mutableStateOf(null)
    }

    val genders: List<GenderAction> = listOf(
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

//    private val uiEditor: UIEditor<UserResponse> = UIEditor(userId == null) {
//        UserResponse(
//            this.userId ?: UUID.randomUUID(),
//            firstNameField.value,
//            surnameField.value,
//            patronymicField.value,
//            Account(emailField.value),
//            avatarUrl.value,
//            false,
//            when (genderField.value) {
//                GenderAction.Undefined -> Gender.UNKNOWN
//                GenderAction.Female -> Gender.FEMALE
//                GenderAction.Male -> Gender.MALE
//            },
//        )
//    }

//    val firstNameField: MutableStateFlow<String> = MutableStateFlow("")

//    val surnameField: MutableStateFlow<String> = MutableStateFlow("")

//    val patronymicField: MutableStateFlow<String> = MutableStateFlow("")

//    val emailField: MutableStateFlow<String> = MutableStateFlow("")

//    val genderField: MutableStateFlow<GenderAction> =
//        MutableStateFlow(GenderAction.Undefined)

//    val avatarUrl: MutableStateFlow<String> = MutableStateFlow("")

//    val accountFieldsVisibility: StateFlow<Boolean> = MutableStateFlow(uiEditor.isNew)

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

    private val validator: CompositeValidator<String> = CompositeValidator(
        listOf(
            ValueValidator(
                value = state::firstName,
                conditions = listOf(
                    Condition(String::isNotEmpty)
                        .observable { isValid ->
                            state.firstNameMessage = getIfNot(isValid) { "Имя обязательно" }
//                            errorState.update {
//                                it.copy(firstNameMessage = if (isValid) null else "Имя обязательно")
//                            }
                        })
            ),
            ValueValidator(
                value = state::surname,
                conditions = listOf(Condition(String::isNotEmpty).observable { isValid ->
                    state.surnameMessage = getIfNot(isValid) { "Фамилия обязательно" }
//                    errorState.update {
//                        it.copy(surnameMessage = if (isValid) null else "Фамилия обязательна")
//                    }
                })
            ),
            emailValidator
        )
    )

    fun onSaveClick() {
        validator.validate()
        saveChanges()
    }

//    init {
//        when (uiEditor.isNew) {
//            false -> {
//                componentScope.launch {
//                    observeUserUseCase(this@UserEditorComponent.userId!!).collect { resource ->
//                        when (resource) {
//                            is Resource.Error -> TODO()
//                            is Resource.Loading -> TODO()
//                            is Resource.Success -> {
//                                resource.value.let { user ->
//                                    uiEditor.oldItem = user
//                                    firstNameField.value = user.firstName
//                                    surnameField.value = user.surname
//                                    patronymicField.value = user.patronymic ?: ""
//                                    genderField.value = when (user.gender) {
//                                        Gender.UNKNOWN -> GenderAction.Undefined
//                                        Gender.FEMALE -> GenderAction.Female
//                                        Gender.MALE -> GenderAction.Male
//                                        else -> throw IllegalArgumentException("Not correct gender")
//                                    }
//                                    emailField.value = user.account.email
//                                    avatarUrl.value = user.avatarUrl
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            true -> {
//
//            }
//        }
//    }

    fun onFirstNameType(firstName: String) {
        state.firstName = firstName
    }

    fun onSurnameType(surname: String) {
        state.surname = surname
    }

    fun onPatronymicType(patronymic: String) {
        state.patronymic = patronymic
    }

    fun onEmailType(email: String) {
        state.email = email
    }

    fun onGenderSelect(gender: GenderAction) {
        state.gender = gender
    }

//    fun onRemoveClick() {
//        confirmInteractor.set(ConfirmState(uiTextOf("Удалить пользователя")))
//        componentScope.launch {
//            if (confirmInteractor.receiveConfirm())
//                removeUserUseCase(userId!!)
//        }
//    }

    private fun saveChanges() {
        componentScope.launch {
            addUserUseCase(
                CreateUserRequest(
                    state.firstName,
                    state.surname,
                    state.patronymic,
                    state.email
                )
            ).onSuccess { onFinish() }
                .onFailure { TODO("Уведомление о подключении к интернету") }
        }
    }

//    init {
//        lifecycle.doOnStart {
//            appBarInteractor.update {
//                it.copy(
//                    title = uiTextOf(
//                        if (uiEditor.isNew) "Новый пользователь"
//                        else "Редактировать пользователя"
//                    )
//                )
//            }
//        }
//    }

    enum class GenderAction(override val title: String, override val iconName: String? = null) :
        MenuAction {
        Undefined(""),
        Male("Мужской"),
        Female("Женский")
    }
}