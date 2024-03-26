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
import com.denchic45.studiversity.common.R
import com.denchic45.studiversity.systemRoleName
import com.denchic45.studiversity.ui.Sidebar
import com.denchic45.studiversity.ui.appbar.ActionMenuItem2
import com.denchic45.studiversity.ui.appbar.AppBarContent
import com.denchic45.studiversity.ui.appbar.updateAnimatedAppBarState
import com.denchic45.studiversity.ui.component.HeaderItem
import com.denchic45.studiversity.ui.uiIconOf
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.stuiversity.api.role.model.Role


@Composable
fun UserEditorSidebar(component: UserEditorComponent) {
    Sidebar(onDismiss = {}, title = { Text(text = "Создать пользователя") }) {

    }
}

@Composable
fun UserEditorScreen(component: UserEditorComponent) {
    val state = remember { component.editingState }
    val allowSave by component.allowSave.collectAsState()
//    val appBarState = LocalAppBarState.current

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

//    LaunchedEffect(allowSave) {
//        appBarState.animateUpdate {
//            content = AppBarContent(
//                title = uiTextOf("Новый пользователь"),
//                actionItems = listOf(
//                    ActionMenuItem2(
//                        icon = uiIconOf(Icons.Default.Done),
//                        enabled = allowSave,
//                        onClick = component::onSaveClick
//                    )
//                )
//            )
//        }
//    }

    val scrollState = rememberScrollState()
    Surface {
        UserEditorContent(
            state = state,
            onFirstNameType = component::onFirstNameChange,
            onSurnameType = component::onSurnameChange,
            onPatronymicType = component::onPatronymicChange,
            onGenderSelect = component::onGenderSelect,
            onRoleSelect = component::onRoleSelect,
            onEmailType = component::onEmailChange,
            modifier = Modifier.verticalScroll(scrollState)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserEditorContent(
    state: EditingUserState,
    onFirstNameType: (String) -> Unit,
    onSurnameType: (String) -> Unit,
    onPatronymicType: (String) -> Unit,
    onGenderSelect: (UserEditorComponent.GenderAction) -> Unit,
    onRoleSelect: (Role) -> Unit,
    onEmailType: (String) -> Unit,
    modifier: Modifier
) {
    Column(
        modifier.padding(horizontal = 16.dp)
    ) {
        HeaderItem("Личные данные")
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
            onValueChange = onPatronymicType,
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
                value = state.gender.title,
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
                state.genders.forEach {
                    DropdownMenuItem(
                        text = { Text(it.title) },
                        onClick = {
                            gendersExpanded = false
                            onGenderSelect(it)
                        }
                    )
                }
            }
        }

        HeaderItem("Вход в аккаунт")
        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailType,
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