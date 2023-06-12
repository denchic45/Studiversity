package com.denchic45.studiversity.ui.usereditor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.common.R
import com.denchic45.studiversity.ui.Sidebar
import com.denchic45.studiversity.ui.appbar2.ActionMenuItem2
import com.denchic45.studiversity.ui.appbar2.AppBarContent
import com.denchic45.studiversity.ui.appbar2.LocalAppBarState
import com.denchic45.studiversity.ui.appbar2.updateAnimatedAppBarState
import com.denchic45.studiversity.ui.appbar2.updateAppBarState
import com.denchic45.studiversity.ui.component.HeaderItemUI
import com.denchic45.studiversity.ui.uiIconOf
import com.denchic45.studiversity.ui.uiTextOf


@Composable
fun UserEditorSidebar(component: UserEditorComponent) {
    Sidebar(onDismiss = {}, title = { Text(text = "Создать пользователя") }) {

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserEditorScreen(component: UserEditorComponent) {
    val state = remember { component.state }
    val allowSave by component.allowSave.collectAsState()
//    val appBarState = LocalAppBarState.current

    updateAnimatedAppBarState(allowSave,AppBarContent(
        title = uiTextOf("Новый пользователь"),
        actionItems = listOf(
            ActionMenuItem2(
                icon = uiIconOf(Icons.Default.Done),
                enabled = allowSave,
                onClick = component::onSaveClick
            )
        ) ))

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
        onFirstNameType = component::onFirstNameType,
        onSurnameType = component::onSurnameType,
        onPatronymicType = component::onPatronymicType,
        onGenderSelect = component::onGenderSelect,
        onEmailType = component::onEmailType,
        modifier = Modifier.verticalScroll(scrollState)
    )
}}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserEditorContent(
    state: UserEditorComponent.CreatableUserState,
    onFirstNameType: (String) -> Unit,
    onSurnameType: (String) -> Unit,
    onPatronymicType: (String) -> Unit,
    onGenderSelect: (UserEditorComponent.GenderAction) -> Unit,
    onEmailType: (String) -> Unit,
    modifier: Modifier
) {
    Column(
        modifier.padding(horizontal = 16.dp)
    ) {
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
        var expandedGenders by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expandedGenders,
            onExpandedChange = { expandedGenders = !expandedGenders }
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
                isError = state.genderMessage != null,
                supportingText = { Text(text = state.genderMessage ?: "") },
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