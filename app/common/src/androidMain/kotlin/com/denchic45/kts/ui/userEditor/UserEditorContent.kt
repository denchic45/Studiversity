package com.denchic45.kts.ui.userEditor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.denchic45.kts.R
import com.denchic45.kts.ui.model.HeaderItem
import com.denchic45.kts.ui.usereditor.UserEditorComponent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserEditorContent(component: UserEditorComponent) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        val photoUrl by component.avatarUrl.collectAsState()
        AsyncImage(
            model = photoUrl,
            contentDescription = "avatar",
            modifier = Modifier
                .padding(top = 48.dp)
                .size(136.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
        Box(
            Modifier
                .size(148.dp)
                .background(Color.Gray)
        )

        val errorState by component.errorState.collectAsState()

        HeaderItem("Личные данные")

        val firstName by component.firstNameField.collectAsState()
        OutlinedTextField(
            value = firstName,
            onValueChange = component::onFirstNameType,
            Modifier.fillMaxWidth(),
            label = { Text("Имя") },
            singleLine = true,
            isError = errorState.firstNameMessage != null,
            supportingText = { Text(errorState.firstNameMessage ?: "") }
        )
        val surname by component.surnameField.collectAsState()
        OutlinedTextField(
            value = surname,
            onValueChange = component::onSurnameType,
            Modifier
                .padding(top = 4.dp)
                .fillMaxWidth(),
            label = { Text("Фамилия") },
            singleLine = true,
            isError = errorState.surnameMessage != null,
            supportingText = { errorState.surnameMessage ?: "" }
        )
        val patronymic by component.patronymicField.collectAsState()
        OutlinedTextField(
            value = patronymic,
            onValueChange = component::onPatronymicType,
            Modifier
                .padding(top = 4.dp)
                .fillMaxWidth(),
            label = { Text("Отчество") },
            placeholder = { Text("Отчество (необязательно)") },
            singleLine = true
        )
        val gender by component.genderField.collectAsState()
        var expandedGenders by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expandedGenders,
            onExpandedChange = { expandedGenders = !expandedGenders },
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            OutlinedTextField(
                value = gender.title,
                onValueChange = { },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                label = { Text("Пол") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expandedGenders
                    )
                },
                singleLine = true,
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )

            ExposedDropdownMenu(
                expanded = expandedGenders,
                onDismissRequest = { expandedGenders = false }) {
                component.genders.forEach {
                    DropdownMenuItem(
                        text = { Text(it.title) },
                        onClick = {
                            component.onGenderSelect(it.action)
                            expandedGenders = false
                        }
                    )
                }
            }
        }

        val accountFieldsVisibility by component.accountFieldsVisibility.collectAsState()
        if (accountFieldsVisibility) {
            HeaderItem("Вход в аккаунт")
            val email by component.emailField.collectAsState()
            OutlinedTextField(
                value = email,
                onValueChange = component::onEmailType,
                Modifier.fillMaxWidth(),
                label = { Text("Почта") },
                leadingIcon = { Icon(painterResource(R.drawable.ic_email), null) },
                singleLine = true,
                isError = errorState.emailMessage != null,
                supportingText = { Text(text = errorState.emailMessage ?: "") }
            )
        }
    }
}