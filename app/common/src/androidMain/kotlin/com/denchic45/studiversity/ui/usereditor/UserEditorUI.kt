package com.denchic45.studiversity.ui.usereditor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.api.role.model.Role
import com.denchic45.studiversity.api.user.model.Gender
import com.denchic45.studiversity.common.R
import com.denchic45.studiversity.displayName
import com.denchic45.studiversity.systemRoleName
import com.denchic45.studiversity.ui.Sidebar
import com.denchic45.studiversity.ui.appbar.ActionMenuItem2
import com.denchic45.studiversity.ui.appbar.AppBarContent
import com.denchic45.studiversity.ui.appbar.updateAnimatedAppBarState
import com.denchic45.studiversity.ui.component.HeaderItem
import com.denchic45.studiversity.ui.confirm.DismissChangesConfirmDialog
import com.denchic45.studiversity.ui.uiIconOf
import com.denchic45.studiversity.ui.uiTextOf


@Composable
fun UserEditorSidebar(component: UserEditorComponent) {
    Sidebar(onDismiss = {}, title = { Text(text = "Создать пользователя") }) {

    }
}

@Composable
fun UserEditorScreen(component: UserEditorComponent) {
    val state = remember { component.editingState }
    val allowSave by component.allowSave.collectAsState()
    val needToConfirmToDismiss by component.needToConfirmToDismiss.collectAsState()

    DismissChangesConfirmDialog(
        show = needToConfirmToDismiss,
        onConfirm = component::onDismissRequest,
        onDismiss = component::onCancelDismiss
    )

    updateAnimatedAppBarState(
        allowSave, AppBarContent(
            title = uiTextOf("Новый пользователь"),
            actionItems = listOf(
                ActionMenuItem2(
                    icon = uiIconOf(Icons.Default.Done),
                    enabled = allowSave,
                    onClick = component::onSaveClick
                )
            )
        )
    )

    val scrollState = rememberScrollState()
    Surface {
        UserEditorContent(
            state = state,
            onFirstNameChange = component::onFirstNameChange,
            onSurnameChange = component::onSurnameChange,
            onPatronymicChange = component::onPatronymicChange,
            onGenderSelect = component::onGenderSelect,
            onRoleSelect = component::onRoleSelect,
            onEmailChange = component::onEmailChange,
            modifier = Modifier.verticalScroll(scrollState)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserEditorContent(
    state: EditingUserState,
    onFirstNameChange: (String) -> Unit,
    onSurnameChange: (String) -> Unit,
    onPatronymicChange: (String) -> Unit,
    onGenderSelect: (Gender) -> Unit,
    onRoleSelect: (Role) -> Unit,
    onEmailChange: (String) -> Unit,
    modifier: Modifier
) {
    Column(
        modifier.padding(horizontal = 16.dp)
    ) {
        HeaderItem("Личные данные")
        OutlinedTextField(
            value = state.firstName,
            onValueChange = onFirstNameChange,
            Modifier.fillMaxWidth(),
            label = { Text("Имя") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            isError = state.firstNameMessage != null,
            supportingText = { Text(state.firstNameMessage ?: "") }
        )
        OutlinedTextField(
            value = state.surname,
            onValueChange = onSurnameChange,
            Modifier
                .padding(top = 4.dp)
                .fillMaxWidth(),
            label = { Text("Фамилия") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            isError = state.surnameMessage != null,
            supportingText = { Text(text = state.surnameMessage ?: "") }
        )
        OutlinedTextField(
            value = state.patronymic,
            onValueChange = onPatronymicChange,
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth(),
            label = { Text("Отчество") },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            supportingText = { Text(text = "") },
            placeholder = { Text("Отчество (необязательно)") },
            singleLine = true
        )

        var gendersExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = gendersExpanded,
            onExpandedChange = { gendersExpanded = !gendersExpanded }
        ) {
            OutlinedTextField(
                value = state.gender.displayName(),
                onValueChange = { },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                label = { Text("Пол") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = gendersExpanded)
                },
                singleLine = true,
                isError = state.genderMessage != null,
                supportingText = { Text(text = state.genderMessage ?: "") },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )

            ExposedDropdownMenu(
                expanded = gendersExpanded,
                onDismissRequest = { gendersExpanded = false }) {

                DropdownMenuItem(
                    text = { Text(Gender.FEMALE.displayName()) },
                    onClick = {
                        gendersExpanded = false
                        onGenderSelect(Gender.FEMALE)
                    }
                )
                DropdownMenuItem(
                    text = { Text(Gender.MALE.displayName()) },
                    onClick = {
                        gendersExpanded = false
                        onGenderSelect(Gender.MALE)
                    }
                )
            }
        }

        HeaderItem("Вход в аккаунт")
        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChange,
            Modifier.fillMaxWidth(),
            label = { Text("Почта") },
            leadingIcon = { Icon(painterResource(R.drawable.ic_email), null) },
            singleLine = true,
            isError = state.emailMessage != null,
            supportingText = { Text(text = state.emailMessage ?: "") }
        )

        var rolesExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = rolesExpanded,
            onExpandedChange = { rolesExpanded = !rolesExpanded }
        ) {
            OutlinedTextField(
                value = state.assignedRoles.let { assigned ->
                    if (assigned.isEmpty()) "Пользователь"
                    else assigned.joinToString(transform = Role::systemRoleName)
                },
                onValueChange = { },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                label = { Text("Роли") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = rolesExpanded)
                },
                singleLine = true,
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )

            ExposedDropdownMenu(
                expanded = rolesExpanded,
                onDismissRequest = { rolesExpanded = false }) {
                state.assignableRoles.forEach {
                    DropdownMenuItem(
                        text = { Text(it.systemRoleName()) },
                        trailingIcon = {
                            if (it in state.assignedRoles)
                                Icon(Icons.Default.Done, contentDescription = "role selected")
                        },
                        onClick = { onRoleSelect(it) }
                    )
                }
            }
        }
    }
}