package com.denchic45.kts.ui.usereditor

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.UIEditor
import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.domain.model.User
import com.denchic45.kts.domain.usecase.ObserveUserUseCase
import com.denchic45.kts.ui.model.MenuAction
import com.denchic45.kts.ui.model.MenuItem
import com.denchic45.kts.util.UUIDS
import com.denchic45.kts.util.componentScope
import com.denchic45.kts.util.randomAlphaNumericString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import java.util.*


@Inject
class UserEditorComponent(
    observeUserUseCase: ObserveUserUseCase,
    componentContext: ComponentContext,
    userId: String?, role: UserRole?, groupId: String?,
) : ComponentContext by componentContext {

    val coroutineScope = componentScope()

    private var userId: String = userId ?: UUIDS.createShort()
    private var role: UserRole = role ?: UserRole.TEACHER

    private val uiEditor: UIEditor<User> = UIEditor(userId == null) {
        User(
            this.userId,
            firstNameField.value,
            surnameField.value,
            patronymicField.value,
            groupId,
            this.role,
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

    val genders = listOf(MenuItem(GenderAction.Male), MenuItem(GenderAction.Female))

    val firstNameField = MutableStateFlow("")

    val surnameField = MutableStateFlow("")

    val patronymicField = MutableStateFlow("")

    val emailField = MutableStateFlow("")

    val passwordField = MutableStateFlow("")

    val genderField = MutableStateFlow(GenderAction.Undefined)

    val roleField = MutableStateFlow(UserRole.TEACHER)

    val groupField = MutableStateFlow("")

    val photoUrl = MutableStateFlow("")

    private var generatedAvatar = true

    val groupFieldVisibility = MutableStateFlow(false)

    val accountFieldsVisibility = MutableStateFlow(true)

    private var admin = false

    init {
        coroutineScope.launch {
            observeUserUseCase(this@UserEditorComponent.userId).collect { user ->
                user?.let {
                    uiEditor.oldItem = user
                    this@UserEditorComponent.role = user.role
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
                    emailField.value = user.email!!
                    photoUrl.value = user.photoUrl
                } ?: run { TODO("finish") }
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

    fun onPasswordGenerateClick() {
        passwordField.value = randomAlphaNumericString(16)
    }
}

enum class GenderAction(override val title: String, override val iconName: String? = null) :
    MenuAction {
    Undefined(""),
    Male("Мужской"),
    Female("Женский")
}