package com.denchic45.studiversity.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.displayName
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.stuiversity.api.user.model.Gender
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalityScreen(component: PersonalityComponent) {
    val state = remember { component.state }
    Column(
        Modifier.fillMaxSize().padding(MaterialTheme.spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        KamelImage(
            resource = asyncPainterResource(state.avatarUrl),
            null,
            Modifier.size(96.dp).clip(CircleShape).background(Color.LightGray),
            contentScale = ContentScale.Crop,
        )
        Text(
            state.fullName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = MaterialTheme.spacing.small)
        )
        Spacer(Modifier.height(MaterialTheme.spacing.large))
        Row {
            OutlinedTextField(
                value = state.firstName,
                onValueChange = component::onFirstNameChange,
                label = { Text("Имя") },
                modifier = Modifier.weight(1f),
                supportingText = state.firstNameMessage?.let { { Text(it) } },
                isError = state.firstNameMessage != null
            )
            Spacer(Modifier.width(MaterialTheme.spacing.normal))
            OutlinedTextField(
                value = state.surname,
                onValueChange = component::onSurnameChange,
                label = { Text("Фамилия") },
                modifier = Modifier.weight(1f),
                supportingText = state.surnameMessage?.let { { Text(it) } },
                isError = state.surnameMessage != null
            )
        }
        Spacer(Modifier.height(MaterialTheme.spacing.small))
        Row {
            OutlinedTextField(
                value = state.patronymic,
                onValueChange = component::onPatronymicChange,
                label = { Text("Отчество") },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(MaterialTheme.spacing.normal))

            var gendersExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = gendersExpanded,
                onExpandedChange = { gendersExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = state.gender.displayName(),
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth()
                        .menuAnchor(),
                    label = { Text("Пол") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(gendersExpanded)
                    },
                    singleLine = true,
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = gendersExpanded,
                    onDismissRequest = { gendersExpanded = false }) {
                    DropdownMenuItem(
                        text = { Text(Gender.FEMALE.displayName()) },
                        onClick = {
                            component.onGenderSelect(Gender.FEMALE)
                            gendersExpanded = false
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(Gender.MALE.displayName()) },
                        onClick = {
                            component.onGenderSelect(Gender.MALE)
                            gendersExpanded = false
                        },
                    )
                }
            }
        }
        Button(
            onClick = component::onSaveClick,
            enabled = component.allowSave(),
            modifier = Modifier.padding(top = MaterialTheme.spacing.normal).align(Alignment.End)
        ) {
            Text("Сохранить")
        }
    }
}