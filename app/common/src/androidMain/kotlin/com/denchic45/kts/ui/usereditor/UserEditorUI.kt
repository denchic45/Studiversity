package com.denchic45.kts.ui.usereditor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denchic45.kts.R
import com.denchic45.kts.ui.Sidebar
import com.denchic45.kts.ui.component.HeaderItemUI


@Composable
fun UserEditorSidebar(component: UserEditorComponent) {
    Sidebar(onDismiss = {}, title = { Text(text = "Создать пользователя") }) {

    }
}

@Composable
fun UserEditorScreen(component: UserEditorComponent) {
    val state = remember { component.state }

    UserEditorContent(
        state = state,
        onFirstNameType = component::onFirstNameType,
        onSurnameType = component::onSurnameType,
        onPatronymicType = component::onPatronymicType,
        onGenderSelect = component::onGenderSelect,
        onEmailType = component::onEmailType
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserEditorContent(
    state: UserEditorComponent.CreatableUserState,
    onFirstNameType: (String) -> Unit,
    onSurnameType: (String) -> Unit,
    onPatronymicType: (String) -> Unit,
    onGenderSelect: (UserEditorComponent.GenderAction) -> Unit,
    onEmailType: (String) -> Unit,
) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        HeaderItemUI("Личные данные")
        OutlinedTextField(
            value = state.firstName,
            onValueChange = onFirstNameType,
            Modifier.fillMaxWidth(),
            label = { Text("Имя") },
            singleLine = true,
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
            isError = state.surnameMessage != null,
            supportingText = { state.surnameMessage ?: "" }
        )
        OutlinedTextField(
            value = state.patronymic,
            onValueChange = onPatronymicType,
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth(),
            label = { Text("Отчество") },
            placeholder = { Text("Отчество (необязательно)") },
            singleLine = true
        )
        var expandedGenders by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expandedGenders,
            onExpandedChange = { expandedGenders = !expandedGenders },
            modifier = Modifier.padding(bottom = 20.dp)
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
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGenders)
                },
                singleLine = true,
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )

            ExposedDropdownMenu(
                expanded = expandedGenders,
                onDismissRequest = { expandedGenders = false }) {
                state.genders.forEach {
                    DropdownMenuItem(
                        text = { Text(it.title) },
                        onClick = {
                            onGenderSelect(it)
                            expandedGenders = false
                        }
                    )
                }
            }
        }

        HeaderItemUI("Вход в аккаунт")
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
    }
}