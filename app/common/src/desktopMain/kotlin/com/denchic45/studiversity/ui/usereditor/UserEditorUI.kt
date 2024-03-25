package com.denchic45.studiversity.ui.usereditor

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.systemRoleName
import com.denchic45.studiversity.ui.LocalAppBarMediator
import com.denchic45.studiversity.ui.component.HeaderItem
import com.denchic45.studiversity.ui.theme.DesktopAppTheme
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.ui.theme.toDrawablePath
import com.denchic45.stuiversity.api.role.model.Role


@Composable
fun UserEditorDialog(component: UserEditorComponent) {
    val state by component.viewState.collectAsState()

    state.onSuccess {
        AlertDialog(
            modifier = Modifier.heightIn(max = 648.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                ) {
                    Text("Создать пользователя")
                    Spacer(Modifier.weight(1f))
                    IconButton(component::onClose) {
                        Icon(Icons.Rounded.Close, "")
                    }
                }
            },
            onDismissRequest = component::onClose,
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    UserEditorContent(
                        state = it,
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
            dismissButton = { TextButton(component::onClose) { Text("Отмена") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserEditorContent(
    state: EditingUserState,
    onFirstNameType: (String) -> Unit,
    onSurnameType: (String) -> Unit,
    onPatronymicType: (String) -> Unit,
    onGenderSelect: (UserEditorComponent.GenderAction) -> Unit,
    onEmailType: (String) -> Unit,
    onRoleSelect: (Role) -> Unit,
    onRemoveUserClick: () -> Unit
) {
    Column {
        HeaderItem("Личные данные")
        Row(
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.normal),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.normal)
        ) {
            OutlinedTextField(
                value = state.firstName,
                onValueChange = onFirstNameType,
                modifier = Modifier.weight(1f),
                label = { Text("Имя") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                isError = state.firstNameMessage != null,
                supportingText = { Text(state.firstNameMessage ?: "") }
            )

            OutlinedTextField(
                value = state.surname,
                onValueChange = onSurnameType,
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
                onValueChange = onPatronymicType,
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
                    supportingText = { Text(state.genderMessage ?: "") },
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
            HeaderItem("Вход в аккаунт")
            OutlinedTextField(
                value = state.email,
                onValueChange = onEmailType,
                Modifier.fillMaxWidth().padding(horizontal = MaterialTheme.spacing.normal),
                label = { Text("Почта") },
                leadingIcon = { Icon(painterResource("ic_email".toDrawablePath()), null) },
                singleLine = true,
                isError = state.emailMessage != null,
                supportingText = { Text(state.emailMessage ?: "") }
            )
        } else {
            var confirmRemove by remember { mutableStateOf(false) }

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
                modifier = Modifier.padding(top = MaterialTheme.spacing.normal)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { confirmRemove = true }
            )
//            FilledTonalButton(
//                onClick = { confirmRemove = true },
//                colors = ButtonDefaults.filledTonalButtonColors(
//                    contentColor = MaterialTheme.colorScheme.error,
//                    containerColor = MaterialTheme.colorScheme.errorContainer
//                ),
//                modifier = Modifier
//                    .padding(MaterialTheme.spacing.normal)
//            ) {
//                Text("Удалить")
//            }
            if (confirmRemove)
                AlertDialog(
                    onDismissRequest = { confirmRemove = false },
                    icon = { Icon(Icons.Outlined.Delete, null) },
                    title = { Text("Удалить пользователя?") },
                    text = {
                        Text(
                            """
                                Удалятся все данные, связанные с данным пользователем.
                                Восстановить пользователя будет невозможно.
                            """.trimIndent()
                        )
                    },
                    confirmButton = {
                        FilledTonalButton(onClick = {
                            confirmRemove = false
                            onRemoveUserClick()
                        }) { Text("Удалить") }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            confirmRemove = false
                        }) { Text("Отмена") }
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
                gender = UserEditorComponent.GenderAction.Male
                email = "danil@mail.ru"
            },
            {}, {}, {}, {}, {}, {}, {}
        )
    }
}