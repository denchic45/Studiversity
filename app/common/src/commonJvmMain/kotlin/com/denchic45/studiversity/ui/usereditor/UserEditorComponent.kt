package com.denchic45.studiversity.ui.usereditor

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.Field
import com.denchic45.studiversity.FieldEditor
import com.denchic45.studiversity.domain.resource.*
import com.denchic45.studiversity.domain.usecase.*
import com.denchic45.studiversity.getOptProperty
import com.denchic45.studiversity.ui.model.MenuAction
import com.denchic45.studiversity.uivalidator.Operator
import com.denchic45.studiversity.uivalidator.condition.Condition
import com.denchic45.studiversity.uivalidator.condition.observable
import com.denchic45.studiversity.uivalidator.getIfNot
import com.denchic45.studiversity.uivalidator.isEmail
import com.denchic45.studiversity.uivalidator.validator.CompositeValidator
import com.denchic45.studiversity.uivalidator.validator.ValueValidator
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.role.model.Role
import com.denchic45.stuiversity.api.user.model.CreateUserRequest
import com.denchic45.stuiversity.api.user.model.Gender
import com.denchic45.stuiversity.api.user.model.UpdateUserRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class UserEditorComponent(
    private val findUserByIdUseCase: FindUserByIdUseCase,
    private val addUserUseCase: AddUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val removeUserUseCase: RemoveUserUseCase,
    findAssignableRolesByUserAndScopeUseCase: FindAssignableRolesByUserAndScopeUseCase,
    findAssignedUserRolesInScopeUseCase: FindAssignedUserRolesInScopeUseCase,
    @Assisted
    private val userId: UUID?,
    @Assisted
   private val onFinish: () -> Unit,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    val allowSave = MutableStateFlow(false)




    private val assignableSystemRoles = findAssignableRolesByUserAndScopeUseCase(null, null)
        .stateInResource(componentScope)

    val editingState = EditingUserState(userId == null)

    val viewState = (userId?.let {
        findUserByIdUseCase(it).mapResource { response ->
            editingState.apply {
                firstName = response.firstName
                surname = response.surname
                patronymic = response.patronymic.orEmpty()
                gender = when (response.gender) {
                    Gender.UNKNOWN -> GenderAction.Undefined
                    Gender.FEMALE -> GenderAction.Female
                    Gender.MALE -> GenderAction.Male
                }
                fieldEditor.updateOldValues()
            }
        }
    } ?: flowOf(Resource.Success(editingState))).stateInResource(componentScope)

    private val fieldEditor = FieldEditor(
        mapOf(
            "firstName" to Field(editingState::firstName),
            "surname" to Field(editingState::surname),
            "patronymic" to Field(editingState::patronymic),
            "gender" to Field(editingState::gender),
            "email" to Field(editingState::email),
            "roles" to Field(editingState::assignedRoles)
        )
    )

    private val emailValidator = ValueValidator(
        value = editingState::email,
        conditions = listOf(
            Condition(String::isNotEmpty).observable { isValid ->
                editingState.emailMessage = getIfNot(isValid) { "Почта обязательна" }
            },
            Condition(String::isEmail).observable { isValid ->
                editingState.emailMessage = getIfNot(isValid) { "Некоректная почта" }
            }
        ),
        operator = Operator.all()
    )

    private val validator = CompositeValidator(
        listOf(
            ValueValidator(
                value = editingState::firstName,
                conditions = listOf(
                    Condition(String::isNotEmpty)
                        .observable { isValid ->
                            editingState.firstNameMessage = getIfNot(isValid) { "Имя обязательно" }
                        })
            ),
            ValueValidator(
                value = editingState::surname,
                conditions = listOf(Condition(String::isNotEmpty).observable { isValid ->
                    editingState.surnameMessage = getIfNot(isValid) { "Фамилия обязательно" }
                })
            ),
            ValueValidator(
                value = editingState::gender,
                conditions = listOf(Condition<GenderAction> { it != GenderAction.Undefined }.observable { isValid ->
                    editingState.genderMessage = getIfNot(isValid) { "Пол обязателен" }
                })
            ),
//            ValueValidator(
//                value = state::assignedRoles,
//                conditions = listOf(Condition<List<Role>> { it.isNotEmpty() }.observable { isValid ->
//                    state.rolesMessage = getIfNot(isValid) { "Выберите хотя бы одну роль" }
//                })
//            ),
            emailValidator
        )
    )

    init {
        assignableSystemRoles.onEach { assignableRoles ->
            assignableRoles.onSuccess {
                editingState.assignableRoles = it
            }
        }.launchIn(componentScope)
    }

    fun onSaveClick() {
        saveChanges()
    }

    fun onFirstNameType(firstName: String) {
        editingState.firstName = firstName
        updateAllowSave()
    }

    fun onSurnameType(surname: String) {
        editingState.surname = surname
        updateAllowSave()
    }

    fun onPatronymicType(patronymic: String) {
        editingState.patronymic = patronymic
    }

    fun onEmailType(email: String) {
        editingState.email = email
        updateAllowSave()
    }

    fun onGenderSelect(gender: GenderAction) {
        editingState.gender = gender
        updateAllowSave()
    }

    fun onRoleSelect(role: Role) {
        val assigned = editingState.assignedRoles
        editingState.assignedRoles = if (role in assigned) {
            assigned - role
        } else {
            assigned + role
        }
    }

    fun onRemoveUserClick() {
        componentScope.launch {
            removeUserUseCase(userId!!).onSuccess { onFinish() }
        }
    }

    private fun updateAllowSave() {
        allowSave.update { fieldEditor.hasChanges() }
    }

    private fun saveChanges() {
        componentScope.launch {
            validator.onValid {
                val result = userId?.let {
                    updateUserUseCase(
                        userId,
                        UpdateUserRequest(
                            fieldEditor.getOptProperty("firstName"),
                            fieldEditor.getOptProperty("surname"),
                            fieldEditor.getOptProperty("patronymic"),
                            fieldEditor.getOptProperty<GenderAction, Gender>("gender") { it.toGender() },
                            fieldEditor.getOptProperty<List<Role>, List<Long>>("roles") { it.map(Role::id) }
                        )
                    )
                } ?: addUserUseCase(
                    CreateUserRequest(
                        editingState.firstName,
                        editingState.surname,
                        editingState.patronymic,
                        editingState.email,
                        editingState.gender.toGender(),
                        editingState.assignedRoles.map(Role::id)
                    )
                )
                result.onSuccess {
                    withContext(Dispatchers.Main.immediate) {
                        onFinish()
                    }
                }.onFailure { }
            }
        }
    }

    private fun GenderAction.toGender() = when (this) {
        GenderAction.Undefined -> Gender.UNKNOWN
        GenderAction.Male -> Gender.MALE
        GenderAction.Female -> Gender.FEMALE
    }

    fun onClose() {
        onFinish()
    }

    enum class GenderAction(
        override val title: String,
        override val iconName: String? = null
    ) : MenuAction {
        Undefined("Не выбран"),
        Male("Мужской"),
        Female("Женский")
    }
}

@Stable
class EditingUserState(val isNew: Boolean) {
    val genders: List<UserEditorComponent.GenderAction> = listOf(
        UserEditorComponent.GenderAction.Male,
        UserEditorComponent.GenderAction.Female,
        UserEditorComponent.GenderAction.Undefined
    )

    var firstName by mutableStateOf("")
    var surname by mutableStateOf("")
    var patronymic by mutableStateOf("")
    var gender by mutableStateOf(UserEditorComponent.GenderAction.Undefined)
    var email by mutableStateOf("")
    var assignedRoles: List<Role> by mutableStateOf(emptyList())

    var assignableRoles: List<Role> by mutableStateOf(emptyList())

    var firstNameMessage: String? by mutableStateOf(null)
    var surnameMessage: String? by mutableStateOf(null)
    var genderMessage: String? by mutableStateOf(null)
    var emailMessage: String? by mutableStateOf(null)
}