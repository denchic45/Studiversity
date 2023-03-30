package com.denchic45.kts.ui.usereditor

import com.denchic45.kts.UIEditor
import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onFailure
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.usecase.AddUserUseCase
import com.denchic45.kts.domain.usecase.ObserveUserUseCase
import com.denchic45.kts.ui.BaseUiComponent
import com.denchic45.kts.ui.model.MenuAction
import com.denchic45.kts.ui.model.MenuItem
import com.denchic45.stuiversity.api.user.model.Account
import com.denchic45.stuiversity.api.user.model.CreateUserRequest
import com.denchic45.stuiversity.api.user.model.Gender
import com.denchic45.stuiversity.api.user.model.UserResponse
import com.denchic45.uivalidator.experimental2.Operator
import com.denchic45.uivalidator.experimental2.condition.Condition
import com.denchic45.uivalidator.experimental2.condition.observable
import com.denchic45.uivalidator.experimental2.validator.CompositeValidator
import com.denchic45.uivalidator.experimental2.validator.ValueValidator
import com.denchic45.uivalidator.isEmail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

 class UserEditorUILogicDelegate(
    override val observeUserUseCase: ObserveUserUseCase,
    override val addUserUseCase: AddUserUseCase,
    override val userId: UUID?,
   override val uiComponent: BaseUiComponent,
   private val _onFinish:()->Unit
) : UserEditorUiLogic {

     override fun onFinish() {
         _onFinish()
     }
     override fun onDeleteUser() {
         TODO("Not yet implemented")
     }
     override val uiEditor: UIEditor<UserResponse> = UIEditor(userId == null) {
        UserResponse(
            this.userId!!,
            firstNameField.value,
            surnameField.value,
            patronymicField.value,
            Account(emailField.value),
            avatarUrl.value,
            when (genderField.value) {
                GenderAction.Undefined -> Gender.UNKNOWN
                GenderAction.Female -> Gender.FEMALE
                GenderAction.Male -> Gender.MALE
            },
        )
    }
    override val errorState = MutableStateFlow(ErrorState())

    override val genders: List<MenuItem<GenderAction>> =
        listOf(MenuItem(GenderAction.Male), MenuItem(GenderAction.Female))

    override val firstNameField: MutableStateFlow<String> = MutableStateFlow("")

    override   val surnameField: MutableStateFlow<String> = MutableStateFlow("")

  override  val patronymicField: MutableStateFlow<String> = MutableStateFlow("")

  override  val emailField: MutableStateFlow<String> = MutableStateFlow("")

   override val genderField: MutableStateFlow<GenderAction> = MutableStateFlow(GenderAction.Undefined)

  override  val avatarUrl: MutableStateFlow<String> = MutableStateFlow("")

    override   val accountFieldsVisibility: StateFlow<Boolean> = MutableStateFlow(uiEditor.isNew)

    override     val toolbarTitle: String = if (uiEditor.isNew) "Новый пользователь" else "Редактировать пользователя"


    private val emailValidator = ValueValidator(
        value = emailField::value,
        conditions = listOf(
            Condition(String::isNotEmpty).observable { isValid ->
                errorState.update {
                    it.copy(emailMessage = if (isValid) null else "Почта обязательна")
                }
            },
            Condition(String::isEmail).observable { isValid ->
                errorState.update { it.copy(emailMessage = if (isValid) null else "Некоректная почта") }
            }
        ),
        operator = Operator.all()
    )

     override val validator: CompositeValidator<String> = CompositeValidator(
        listOf(
            ValueValidator(
                value = firstNameField::value,
                conditions = listOf(Condition(String::isNotEmpty)
                    .observable { isValid ->
                        errorState.update {
                            it.copy(firstNameMessage = if (isValid) null else "Имя обязательно")
                        }
                    })
            ),
            ValueValidator(
                value = surnameField::value,
                conditions = listOf(Condition(String::isNotEmpty).observable { isValid ->
                    errorState.update {
                        it.copy(surnameMessage = if (isValid) null else "Фамилия обязательна")
                    }
                })
            )
        )
    )

     init {
        when (uiEditor.isNew) {
            false -> {
                componentScope.launch {
                    observeUserUseCase(this@UserEditorUILogicDelegate.userId!!).collect { resource ->
                        when (resource) {
                            is Resource.Error -> TODO()
                            is Resource.Loading -> TODO()
                            is Resource.Success -> {
                                resource.value.let { user ->
                                    uiEditor.oldItem = user
                                    firstNameField.value = user.firstName
                                    surnameField.value = user.surname
                                    patronymicField.value = user.patronymic ?: ""
                                    genderField.value = when (user.gender) {
                                        Gender.UNKNOWN -> GenderAction.Undefined
                                        Gender.FEMALE -> GenderAction.Female
                                        Gender.MALE -> GenderAction.Male
                                        else -> throw IllegalArgumentException("Not correct gender")
                                    }
                                    emailField.value = user.account.email
                                    avatarUrl.value = user.avatarUrl
                                }
                            }
                        }
                    }
                }
            }
            true -> {

            }
        }
    }
}

interface UserEditorUiLogic {
    val observeUserUseCase: ObserveUserUseCase
    val addUserUseCase: AddUserUseCase
    val userId: UUID?
    val uiComponent: BaseUiComponent

    fun onFinish()

    val uiEditor: UIEditor<UserResponse>

    val errorState: MutableStateFlow<ErrorState>

    val genders: List<MenuItem<GenderAction>>

    val firstNameField: MutableStateFlow<String>

    val surnameField: MutableStateFlow<String>

    val patronymicField: MutableStateFlow<String>

    val emailField: MutableStateFlow<String>

    val genderField: MutableStateFlow<GenderAction>

    private fun UserRole.toRoleAction(): RoleAction = when (this) {
        UserRole.STUDENT -> RoleAction.Student
        UserRole.TEACHER -> RoleAction.Teacher
        UserRole.HEAD_TEACHER -> RoleAction.HeadTeacher
    }

    val avatarUrl: MutableStateFlow<String>


    val accountFieldsVisibility: StateFlow<Boolean>

    val toolbarTitle: String

    val validator: CompositeValidator<String>

    fun onFirstNameType(firstName: String) {
        firstNameField.value = firstName
    }

    fun onSurnameType(surname: String) {
        surnameField.value = surname
    }

    fun onPatronymicType(patronymic: String) {
        patronymicField.value = patronymic
    }

    fun onEmailType(email: String) {
        emailField.value = email
    }

    fun onGenderSelect(action: GenderAction) {
        genderField.value = action
    }

    fun onSaveClick() {
        validator.validate()
        saveChanges()
    }

    fun onCloseClick() = onFinish()

    private fun saveChanges() {
        componentScope.launch {
            addUserUseCase(
                CreateUserRequest(
                    firstNameField.value,
                    surnameField.value,
                    patronymicField.value,
                    emailField.value
                )
            ).onSuccess { onFinish() }
                .onFailure { TODO("Уведомление о подключении к интернету") }
        }
    }

    fun onRemoveClick() {
        onDeleteUser()
    }

    abstract fun onDeleteUser()
}

data class ErrorState(
    val firstNameMessage: String? = null,
    val surnameMessage: String? = null,
    val groupMessage: String? = null,
    val emailMessage: String? = null,
    val passwordMessage: String? = null,
    val isValid: Boolean = true,
)

enum class GenderAction(override val title: String, override val iconName: String? = null) :
    MenuAction {
    Undefined(""),
    Male("Мужской"),
    Female("Женский")
}

enum class RoleAction(override val title: String, override val iconName: String? = null) :
    MenuAction {
    Student("Студент"),
    Teacher("Преподватель"),
    HeadTeacher("Завуч")
}