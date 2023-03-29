package com.denchic45.kts.ui.usereditor

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.denchic45.kts.UIEditor
import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onFailure
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.AddUserUseCase
import com.denchic45.kts.domain.usecase.FindStudyGroupByIdUseCase
import com.denchic45.kts.domain.usecase.ObserveUserUseCase
import com.denchic45.kts.ui.model.MenuAction
import com.denchic45.kts.ui.model.MenuItem
import com.denchic45.kts.ui.navigation.ConfirmConfig
import com.denchic45.kts.ui.navigation.OverlayConfig
import com.denchic45.kts.ui.navigation.UserEditorConfig
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class UserEditorComponent(
    observeUserUseCase: ObserveUserUseCase,
    private val addUserUseCase: AddUserUseCase,
    private val findStudyGroupByIdUseCase: FindStudyGroupByIdUseCase,
    private val overlayNavigation: OverlayNavigation<OverlayConfig>,
    componentContext: ComponentContext,
    private val onFinish: () -> Unit,
    private val config: UserEditorConfig,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    private val userId: UUID? = config.userId

    private val uiEditor: UIEditor<UserResponse> = UIEditor(config.userId == null) {
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

    val errorState = MutableStateFlow(ErrorState())

    private val groupId = MutableStateFlow(config.groupId)

    val genders = listOf(MenuItem(GenderAction.Male), MenuItem(GenderAction.Female))

    val firstNameField = MutableStateFlow("")

    val surnameField = MutableStateFlow("")

    val patronymicField = MutableStateFlow("")

    val emailField = MutableStateFlow("")

    val genderField = MutableStateFlow(GenderAction.Undefined)

    val availableRoles: StateFlow<List<MenuItem<RoleAction>>?> = MutableStateFlow(
        when (config.role) {
            UserRole.TEACHER,
            UserRole.HEAD_TEACHER,
            -> listOf(
                MenuItem(RoleAction.Teacher),
                MenuItem(RoleAction.HeadTeacher)
            )
            UserRole.STUDENT -> null
        }
    )

//    val roleField: StateFlow<RoleAction> =
//        selectedRole
//            .map { it.toRoleAction() }
//            .stateIn(componentScope, SharingStarted.Lazily, selectedRole.value.toRoleAction())

    private fun UserRole.toRoleAction(): RoleAction = when (this) {
        UserRole.STUDENT -> RoleAction.Student
        UserRole.TEACHER -> RoleAction.Teacher
        UserRole.HEAD_TEACHER -> RoleAction.HeadTeacher
    }

    val groupField: StateFlow<Resource<StudyGroupResponse>> = groupId.filterNotNull()
        .map { findStudyGroupByIdUseCase(it) }
        .stateInResource(componentScope)

    val avatarUrl = MutableStateFlow("")

    val groupFieldVisibility: StateFlow<Boolean> = MutableStateFlow(config.role == UserRole.STUDENT)

    val accountFieldsVisibility: StateFlow<Boolean> = MutableStateFlow(uiEditor.isNew)

    val toolbarTitle = if (uiEditor.isNew) "Новый пользователь" else "Редактировать пользователя"

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

    private val validator = CompositeValidator(
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
                    observeUserUseCase(this@UserEditorComponent.userId!!).collect { resource ->
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

    fun onFirstNameType(firstName: String) {
        firstNameField.value = firstName
    }

    fun onSurnameType(surname: String) {
        surnameField.value = surname
    }

    fun onPatronymicType(patronymic: String) {
        patronymicField.value = patronymic
    }

    // TODO: fix
    fun onRoleSelect(roleAction: RoleAction) {
//        selectedRole.value = when (roleAction) {
//            RoleAction.Student -> UserRole.STUDENT
//            RoleAction.Teacher -> UserRole.TEACHER
//            RoleAction.HeadTeacher -> UserRole.HEAD_TEACHER
//        }
    }

    fun onEmailType(email: String) {
        emailField.value = email
    }

    fun onGenderSelect(action: GenderAction) {
        genderField.value = action
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
        overlayNavigation.activate(ConfirmConfig("Удалить студента", "Вы уверены?"))
    }
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