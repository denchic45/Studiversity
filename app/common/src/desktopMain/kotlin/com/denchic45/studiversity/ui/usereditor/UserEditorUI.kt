package com.denchic45.studiversity.ui.usereditor

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.api.role.model.Role
import com.denchic45.studiversity.api.user.model.Gender
import com.denchic45.studiversity.displayName
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.systemRoleName
import com.denchic45.studiversity.ui.component.DeleteListItem
import com.denchic45.studiversity.ui.component.HeaderItem
import com.denchic45.studiversity.ui.component.TitleAlertDialog
import com.denchic45.studiversity.ui.confirm.DismissChangesConfirmDialog
import com.denchic45.studiversity.ui.theme.DesktopAppTheme
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.ui.theme.toDrawablePath
import com.denchic45.studiversity.ui.user.RemoveUserConfirmDialog

@Composable
fun UserEditorDialog(component: UserEditorComponent) {
    val stateResource by component.stateResource.collectAsState()
    val allowSave by component.allowSave.collectAsState()
    val needToConfirmToDismiss by component.needToConfirmToDismiss.collectAsState()

    DismissChangesConfirmDialog(
        show = needToConfirmToDismiss,
        onConfirm = component::onDismissRequest,
        onDismiss = component::onCancelDismiss
    )

    stateResource.onSuccess {
        AlertDialog(
            modifier = Modifier.heightIn(max = 648.dp),
            title = {
                TitleAlertDialog(
                    text = if (it.isNew) "Создать пользователя" else "Изменить пользователя",
                    onClose = component::onDismissRequest
                )
            },
            onDismissRequest = component::onDismissRequest,
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    UserEditorContent(
                        state = it,
                        onFirstNameChange = component::onFirstNameChange,
                        onSurnameChange = component::onSurnameChange,
                        onPatronymicChange = component::onPatronymicChange,
                        onGenderSelect = component::onGenderSelect,
                        onEmailChange = component::onEmailChange,
                        onRoleSelect = component::onRoleSelect,
                        onRemoveClick = component::onRemoveClick
                    )
                }
            },
            confirmButton = { TextButton(component::onSaveClick, enabled = allowSave) { Text("Сохранить") } },
            dismissButton = { TextButton(component::onDismissRequest) { Text("Отмена") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserEditorContent(
    state: EditingUserState,
    onFirstNameChange: (String) -> Unit,
    onSurnameChange: (String) -> Unit,
    onPatronymicChange: (String) -> Unit,
    onGenderSelect: (Gender) -> Unit,
    onEmailChange: (String) -> Unit,
    onRoleSelect: (Role) -> Unit,
    onRemoveClick: () -> Unit
) {
    Column {
        HeaderItem("Личные данные")
        Row(
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.normal),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.normal)
        ) {
            OutlinedTextField(
                value = state.firstName,
                onValueChange = onFirstNameChange,
                modifier = Modifier.weight(1f),
                label = { Text("Имя") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                isError = state.firstNameMessage != null,
                supportingText = { Text(state.firstNameMessage ?: "") }
            )

            OutlinedTextField(
                value = state.surname,
                onValueChange = onSurnameChange,
                modifier = Modifier.weight(1f),
                label = { Text("Фамилия") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                isError = state.surnameMessage != null,
                supportingText = { Text(state.surnameMessage ?: "") }
            )
        }

        Row(
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.normal),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.normal)
        ) {
            OutlinedTextField(
                value = state.patronymic,
                onValueChange = onPatronymicChange,
                modifier = Modifier.weight(1f),
                label = { Text("Отчество") },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                placeholder = { Text("Отчество (необязательно)") },
                singleLine = true
            )
            var gendersExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = gendersExpanded,
                onExpandedChange = { gendersExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = state.gender.displayName(),
                    onValueChange = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    label = { Text("Пол") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = gendersExpanded
                        )
                    },
                    singleLine = true,
                    supportingText = { Text(state.genderMessage ?: "") },
                    isError = state.genderMessage != null,
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
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(Gender.MALE.displayName()) },
                        onClick = {
                            gendersExpanded = false
                            onGenderSelect(Gender.MALE)
                        },
                    )
                }
            }
        }

        var rolesExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = rolesExpanded,
            onExpandedChange = { rolesExpanded = it },
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.normal)
        ) {
            OutlinedTextField(
                value = state.assignedRoles.joinToString(transform = Role::systemRoleName),
                onValueChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                label = { Text("Роли") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(rolesExpanded)
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
                        onClick = {
                            rolesExpanded = false
                            onRoleSelect(it)
                        }
                    )
                }
            }
        }

        if (state.isNew) {
            HeaderItem("Вход в аккаунт")
            OutlinedTextField(
                value = state.email,
                onValueChange = onEmailChange,
                Modifier.fillMaxWidth().padding(horizontal = MaterialTheme.spacing.normal),
                label = { Text("Почта") },
                leadingIcon = { Icon(painterResource("ic_email".toDrawablePath()), null) },
                singleLine = true,
                isError = state.emailMessage != null,
                supportingText = { Text(state.emailMessage ?: "") }
            )
        } else {
            var showDeleteConfirmation by remember { mutableStateOf(false) }

            DeleteListItem(
                title = "Удалить пользователя",
                text = "Восстановление будет невозможно",
                onClick = { showDeleteConfirmation = true },
                modifier = Modifier.padding(top = MaterialTheme.spacing.normal)
            )

            ListItem(
                headlineContent = { Text("Удалить пользователя") },
                supportingContent = { Text("Восстановление будет невозможно") },
                leadingContent = {
                    Icon(
                        Icons.Outlined.Delete,
                        null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { showDeleteConfirmation = true }
            )

            if (showDeleteConfirmation)
                RemoveUserConfirmDialog(
                    onConfirm = {
                        showDeleteConfirmation = false
                        onRemoveClick()
                    },
                    onDismiss = {
                        showDeleteConfirmation = false
                    }
                )
        }
    }
}

@Preview
@Composable
fun UserEditorPreview() {
    DesktopAppTheme {
        UserEditorContent(
            state = EditingUserState(false).apply {
                firstName = "Козлов"
                surname = "Даниил"
                patronymic = "Васильевич"
                gender = Gender.MALE
                email = "danil@mail.ru"
            },
            {}, {}, {}, {}, {}, {}, {}
        )
    }
}