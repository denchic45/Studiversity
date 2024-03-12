package com.denchic45.studiversity.ui.usereditor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.systemRoleName
import com.denchic45.studiversity.ui.LocalAppBarMediator
import com.denchic45.studiversity.ui.component.HeaderItemUI
import com.denchic45.studiversity.ui.components.dialog.AlertDialog
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.ui.theme.toDrawablePath
import com.denchic45.stuiversity.api.role.model.Role

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserEditorSidebar(
    component: UserEditorComponent,
    modifier: Modifier = Modifier,
) {
    val appBarState = LocalAppBarMediator.current
    Column(modifier) {
        TopAppBar(
            title = {
                Text(appBarState.title)
            },
            navigationIcon = {
                IconButton(onClick = { component.onFinish() }) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            actions = {
                Button(
                    onClick = component::onSaveClick,
                    Modifier.padding(horizontal = 16.dp)
                ) { Text("Сохранить") }
            }, colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent)
        )
        Column(Modifier.verticalScroll(rememberScrollState())) {
            TODO()
//            UserEditorContent()
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun UserEditorDialog(
    component: UserEditorComponent,
    onDismissRequest: () -> Unit,
) {
    val state = remember { component.editingState }
    AlertDialog(
        modifier = Modifier.heightIn(max = 648.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
            ) {
                Text("Создать пользователя")
                Spacer(Modifier.weight(1f))
                IconButton({ onDismissRequest() }) {
                    Icon(Icons.Rounded.Close, "")
                }
            }
        },
        onDismissRequest = onDismissRequest,
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                UserEditorContent(
                    state = state,
                    onFirstNameType = component::onFirstNameType,
                    onSurnameType = component::onSurnameType,
                    onPatronymicType = component::onPatronymicType,
                    onGenderSelect = component::onGenderSelect,
                    onEmailType = component::onEmailType,
                    onRoleSelect = component::onRoleSelect,
                    onRemoveUserClick = component::onRemoveUserClick
                )
            }
        },
        confirmButton = { TextButton(component::onSaveClick) { Text("Сохранить") } },
        dismissButton = { TextButton({ onDismissRequest() }) { Text("Отмена") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserEditorContent(
    state: UserEditorComponent.EditingUser,
    onFirstNameType: (String) -> Unit,
    onSurnameType: (String) -> Unit,
    onPatronymicType: (String) -> Unit,
    onGenderSelect: (UserEditorComponent.GenderAction) -> Unit,
    onEmailType: (String) -> Unit,
    onRoleSelect: (Role) -> Unit,
    onRemoveUserClick: () -> Unit
) {
    Column(Modifier.padding(horizontal = MaterialTheme.spacing.normal)) {
        HeaderItemUI("Личные данные")
        OutlinedTextField(
            value = state.firstName,
            onValueChange = onFirstNameType,
            Modifier.fillMaxWidth(),
            label = { Text("Имя") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            isError = state.firstNameMessage != null,
            supportingText = { Text(state.firstNameMessage ?: "") }
        )

        OutlinedTextField(
            value = state.surname,
            onValueChange = onSurnameType,
            Modifier.padding(top = 4.dp).fillMaxWidth(),
            label = { Text("Фамилия") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            isError = state.surnameMessage != null,
            supportingText = { Text(state.surnameMessage ?: "") }
        )

        OutlinedTextField(
            value = state.patronymic,
            onValueChange = onPatronymicType,
            Modifier.padding(top = 4.dp).fillMaxWidth(),
            label = { Text("Отчество") },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            placeholder = { Text("Отчество (необязательно)") },
            singleLine = true
        )
        var gendersExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = gendersExpanded,
            onExpandedChange = { gendersExpanded = it },
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.normal)
        ) {
            OutlinedTextField(
                value = state.gender.title,
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
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )

            ExposedDropdownMenu(
                expanded = gendersExpanded,
                onDismissRequest = { gendersExpanded = false }) {
                state.genders.forEach {
                    DropdownMenuItem(
                        text = { Text(it.title) },
                        onClick = { onGenderSelect(it) },
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
                label = { Text("Роли") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = rolesExpanded
                    )
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
                            onRoleSelect(it)
                            rolesExpanded = false
                        }
                    )
                }
            }
        }

        if (state.isNew) {
            HeaderItemUI("Вход в аккаунт")
            OutlinedTextField(
                value = state.email,
                onValueChange = onEmailType,
                Modifier.fillMaxWidth(),
                label = { Text("Почта") },
                leadingIcon = { Icon(painterResource("ic_email".toDrawablePath()), null) },
                singleLine = true,
                isError = state.emailMessage != null,
                supportingText = { Text(state.emailMessage ?: "") }
            )
        } else {
            var confirmRemove by remember { mutableStateOf(false) }
            OutlinedButton(onClick = { confirmRemove = true }) {
                Text("Удалить")
            }

            if (confirmRemove)
                AlertDialog(
                    onDismissRequest = { confirmRemove = false },
                    title = { Text("Удалить пользователя?") },
                    text = { Text("Восстановить пользователя и его ") },
                    confirmButton = {
                        Button(onClick = {
                            confirmRemove = false
                            onRemoveUserClick()
                        }) { Text("Да") }
                    },
                    dismissButton = { TextButton(onClick = { confirmRemove = false }) { Text("Отмена") } }
                )
        }
    }
}