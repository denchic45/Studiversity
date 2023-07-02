package com.denchic45.studiversity.ui.usereditor

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.Field
import com.denchic45.studiversity.FieldEditor
import com.denchic45.studiversity.domain.onFailure
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.domain.usecase.AddUserUseCase
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
    class CreatableUserState(val genders: List<GenderAction>, val roles: List<RoleAction>) {

        var firstName by mutableStateOf("")
        var surname by mutableStateOf("")
        var patronymic by mutableStateOf("")
        var gender by mutableStateOf(GenderAction.Undefined)
        var email by mutableStateOf("")
        var assignedRoles: List<Role> by mutableStateOf(emptyList())

        fun getRoleNameOf(role: Role): String {
            return roles.first { it.role == role }.title
        }

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
    val state = CreatableUserState(
        genders,
        listOf(RoleAction.TeacherPerson, RoleAction.StudentPerson, RoleAction.Moderator)
    )

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
//            ValueValidator(
//                value = state::assignedRoles,
//                conditions = listOf(Condition<List<Role>> {it.isNotEmpty()}.observable {isValid->
//                    state.rolesMessage = getIfNot(isValid) { "Выберите хотя бы одну роль" }
//                })
//            )
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

    fun onRoleSelect(action: RoleAction) {
        val assigned = state.assignedRoles
        state.assignedRoles = if (action.role in assigned) {
            assigned - action.role
        } else {
            assigned + action.role
        }
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
                        },
                        state.assignedRoles.map(Role::id)
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

    enum class RoleAction(val title: String, val role: Role) {
        //        User("Пользователь", Role.User),
        TeacherPerson("Преподаватель", Role.TeacherPerson),
        StudentPerson("Учащийся", Role.StudentPerson),
        Moderator("Модератор", Role.Moderator)
    }
}