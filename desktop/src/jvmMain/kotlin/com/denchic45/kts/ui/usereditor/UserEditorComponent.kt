package com.denchic45.kts.ui.usereditor

import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.ui.model.MenuAction
import com.denchic45.kts.ui.model.MenuItem
import com.denchic45.kts.util.randomAlphaNumericString
import kotlinx.coroutines.flow.MutableStateFlow
import me.tatarka.inject.annotations.Inject


@Inject
class UserEditorComponent(userId: String?, role: UserRole, groupId: String?) {
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

    val groupFieldVisibility = MutableStateFlow(false)

    val accountFieldsVisibility = MutableStateFlow(true)

    init {

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