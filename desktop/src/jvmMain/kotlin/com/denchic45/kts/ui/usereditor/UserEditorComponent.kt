package com.denchic45.kts.ui.usereditor

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.UIEditor
import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.domain.model.GroupHeader
import com.denchic45.kts.domain.model.User
import com.denchic45.kts.domain.usecase.AddUserUseCase
import com.denchic45.kts.domain.usecase.ObserveGroupInfoUseCase
import com.denchic45.kts.domain.usecase.ObserveUserUseCase
import com.denchic45.kts.domain.usecase.UpdateUserUseCase
import com.denchic45.kts.ui.model.MenuAction
import com.denchic45.kts.ui.model.MenuItem
import com.denchic45.kts.util.UUIDS
import com.denchic45.kts.util.componentScope
import com.denchic45.kts.util.randomAlphaNumericString
import com.denchic45.uivalidator.experimental.Condition
import com.denchic45.uivalidator.experimental.Operator
import com.denchic45.uivalidator.experimental.Validator
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import java.util.*


@Inject
class UserEditorComponent(
    observeUserUseCase: ObserveUserUseCase,
    private val addUserUseCase: AddUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val observeGroupInfoUseCase: ObserveGroupInfoUseCase,
    componentContext: ComponentContext,
    private val onFinish: () -> Unit,
    userId: String?, role: UserRole, groupId: String?,
) : ComponentContext by componentContext {

    private val componentScope = componentScope(Dispatchers.Default + SupervisorJob())

    private var userId: String = userId ?: UUIDS.createShort()

    private val uiEditor: UIEditor<User> = UIEditor(userId == null) {
        User(
            this.userId,
            firstNameField.value,
            surnameField.value,
            patronymicField.value,
            groupId,
            selectedRole.value,
            emailField.value,
            photoUrl.value,
            Date(),
            when (genderField.value) {
                GenderAction.Undefined -> 0
                GenderAction.Female -> 1
                GenderAction.Male -> 2
            },
            generatedAvatar,
            admin
        )
    }

    private val groupId = MutableStateFlow(groupId)

    val genders = listOf(MenuItem(GenderAction.Male), MenuItem(GenderAction.Female))

    val firstNameField = MutableStateFlow("")

    val surnameField = MutableStateFlow("")

    val patronymicField = MutableStateFlow("")

    val emailField = MutableStateFlow("")

    val passwordField = MutableStateFlow("")

    val genderField = MutableStateFlow(GenderAction.Undefined)

    val availableRoles: StateFlow<List<MenuItem<RoleAction>>?> = MutableStateFlow(
        when (role) {
            UserRole.TEACHER,
            UserRole.HEAD_TEACHER,
            -> listOf(
                MenuItem(RoleAction.Teacher),
                MenuItem(RoleAction.HeadTeacher)
            )
            UserRole.STUDENT -> null
        }
    )


    private val selectedRole = MutableStateFlow(role)

    val roleField: StateFlow<RoleAction> =
        selectedRole
            .map { it.toRoleAction() }
            .stateIn(componentScope, SharingStarted.Lazily, selectedRole.value.toRoleAction())

    private fun UserRole.toRoleAction(): RoleAction = when (this) {
        UserRole.STUDENT -> RoleAction.Student
        UserRole.TEACHER -> RoleAction.Teacher
        UserRole.HEAD_TEACHER -> RoleAction.HeadTeacher
    }

    val groupField: StateFlow<GroupHeader> =
        this.groupId.filterNotNull().flatMapLatest { observeGroupInfoUseCase(it) }
            .stateIn(componentScope, SharingStarted.Lazily, GroupHeader.createEmpty())

    val photoUrl = MutableStateFlow("")

    private var generatedAvatar = true

    val groupFieldVisibility: StateFlow<Boolean> = MutableStateFlow(role == UserRole.STUDENT)

    val accountFieldsVisibility: StateFlow<Boolean> = MutableStateFlow(uiEditor.isNew)

    private var admin = false

    val toolbarTitle = when (selectedRole.value) {
        UserRole.STUDENT -> if (uiEditor.isNew) "Новый студент" else "Редактировать студента"
        UserRole.TEACHER, UserRole.HEAD_TEACHER -> {
            if (uiEditor.isNew) "Новый руководитель" else "Редактировать руководителя"
        }
    }

    private val emailValidator = Validator(
        conditions = listOf(
            Condition(
                value = emailField::value,
                predicate = String::isNotEmpty
            ),
            Condition(
                value = emailField::value,
                predicate = { TODO("Убедиться, что это настоящий email") }
            ) {}
        )
    )
    private val passwordValidator = Validator(
        conditions = listOf(
            Condition(
                value = passwordField::value,
                predicate = String::isNotEmpty
            ) {},
            Condition(
                value = passwordField::value,
                predicate = { it.length >= 6 }
            ) {},
            Condition(
                value = passwordField::value,
                predicate = {
                    TODO("Проверить, что содержаться буквы в верхнем и нижнем регистрах и цифры")
                }
            ) {},
        )
    )
    val validator = Validator(conditions = listOf(
        Condition(
            value = firstNameField::value,
            predicate = String::isNotEmpty
        ) {},
        Condition(
            value = surnameField::value,
            predicate = String::isNotEmpty
        ) {},
        Condition(
            value = groupField::value,
            predicate = { group ->
                group != GroupHeader.createEmpty() || selectedRole.value != UserRole.STUDENT
            }
        ) {},
        Validator(
            conditions = listOf(
                Condition(value = accountFieldsVisibility::value, predicate = { !it }) {},
                Validator(
                    conditions = listOf(emailValidator, passwordValidator)
                )
            ),
            operator = Operator.Any
        )
    )
    )

    init {
        when (uiEditor.isNew) {
            false -> {
                componentScope.launch {
                    observeUserUseCase(this@UserEditorComponent.userId).collect { user ->
                        user?.let {
                            uiEditor.oldItem = user
                            admin = user.admin
                            generatedAvatar = user.generatedAvatar
                            firstNameField.value = user.firstName
                            surnameField.value = user.surname
                            patronymicField.value = user.patronymic ?: ""
                            genderField.value = when (it.gender) {
                                0 -> GenderAction.Undefined
                                1 -> GenderAction.Female
                                2 -> GenderAction.Male
                                else -> throw IllegalArgumentException("Not correct gender")
                            }
                            selectedRole.value = user.role
                            emailField.value = user.email
                            photoUrl.value = user.photoUrl
                        } ?: onFinish()
                    }
                }
            }
            true -> {

            }
        }
    }

    fun onFirstNameType(firstName: String) {
        firstNameField.value = firstName
    }

    fun onSurnameType(surname: String) {
        surnameField.value = surname
    }

    fun onPatronymicType(patronymic: String) {
        patronymicField.value = patronymic
    }

    fun onRoleSelect(roleAction: RoleAction) {
        selectedRole.value = when (roleAction) {
            RoleAction.Student -> UserRole.STUDENT
            RoleAction.Teacher -> UserRole.TEACHER
            RoleAction.HeadTeacher -> UserRole.HEAD_TEACHER
        }
    }

    fun onEmailType(email: String) {
        emailField.value = email
    }

    fun onGenderSelect(action: GenderAction) {
        genderField.value = action
    }

    fun onPasswordType(password: String) {
        passwordField.value = password
    }

    fun onGroupClick() {
//        viewModelScope.launch {
//            navigateTo(MobileNavigationDirections.actionGlobalGroupChooserFragment())
//            groupChooserInteractor.receiveSelectedGroup().let {
//                groupId = it.id
//                groupField.value = it.name
//            }
//        }
    }

    fun onSaveClick() {
        saveChanges()
    }

    fun onPasswordGenerateClick() {
        passwordField.value = randomAlphaNumericString(16)
    }

    private fun saveChanges() {
        componentScope.launch {
            if (uiEditor.isNew) {
                addUserUseCase(uiEditor.item, passwordField.value)
            } else {
                updateUserUseCase(uiEditor.item)
            }
                .onSuccess { onFinish() }
                .onFailure { TODO("Уведомление о подключении к интернету") }
        }
    }
}

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