package com.denchic45.kts.ui.usereditor

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.denchic45.kts.ui.components.HeaderItem
import com.denchic45.kts.ui.components.Spinner
import com.denchic45.kts.ui.components.SupportingText
import com.denchic45.kts.ui.components.dialog.AlertDialog
import com.denchic45.kts.ui.theme.TextM2
import com.denchic45.kts.ui.theme.toDrawablePath
import com.denchic45.kts.util.AsyncImage
import com.denchic45.kts.util.loadImageBitmap

@Composable
fun UserEditorScreen(component: UserEditorComponent, modifier: Modifier = Modifier) {
    Column(modifier) {
        SmallTopAppBar(
            title = { Text(component.toolbarTitle) },
            navigationIcon = {
                IconButton(onClick = component::onCloseClick) {
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
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent)
        )
        UserEditorContent(component)
    }
}

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class
)
@Composable
fun UserEditorDialog(component: UserEditorComponent, onDismissRequest: () -> Unit) {
    AlertDialog(
        modifier = Modifier.heightIn(max = 648.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
            ) {
                Text(component.toolbarTitle)
                Spacer(Modifier.weight(0.1f))
                IconButton({}) {
                    Icon(Icons.Rounded.Close, "")
                }
            }
        },
        onDismissRequest = onDismissRequest,
        text = { UserEditorContent(component) },
        confirmButton = { Button({}) { Text("Confirm") } },
        dismissButton = { Button({}) { Text("Close") } },
        optionalButton = { Button({}) { Text("Удалить") } }
    )
}

@Composable
private fun UserEditorContent(component: UserEditorComponent) {
    Column(Modifier.padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
        val photoUrl by component.photoUrl.collectAsState()

        AsyncImage(
            load = { loadImageBitmap(photoUrl) },
            painterFor = { BitmapPainter(it) },
            key = photoUrl,
            null,
            modifier = Modifier.padding(top = 48.dp).size(136.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        { Box(Modifier.size(148.dp).background(Color.Gray)) }

        val errorState by component.errorState.collectAsState()

        HeaderItem("Личные данные")
        val firstName by component.firstNameField.collectAsState()

        OutlinedTextField(
            value = firstName,
            onValueChange = component::onFirstNameType,
            Modifier.fillMaxWidth(),
            label = { TextM2("Имя") },
            singleLine = true,
            isError = errorState.firstNameMessage != null,
        )
        SupportingText(errorState.firstNameMessage ?: "", true)
        val surname by component.surnameField.collectAsState()
        OutlinedTextField(
            value = surname,
            onValueChange = component::onSurnameType,
            Modifier.padding(top = 4.dp).fillMaxWidth(),
            label = { TextM2("Фамилия") },
            singleLine = true,
            isError = errorState.surnameMessage != null,
        )
        SupportingText(errorState.surnameMessage ?: "", true)
        val patronymic by component.patronymicField.collectAsState()
        OutlinedTextField(
            value = patronymic,
            onValueChange = component::onPatronymicType,
            Modifier.padding(top = 4.dp).fillMaxWidth(),
            label = { TextM2("Отчество") },
            placeholder = { TextM2("Отчество (необязательно)") },
            singleLine = true
        )
        val gender by component.genderField.collectAsState()
        var expandedGenders by remember { mutableStateOf(false) }
        Spinner(
            items = component.genders,
            onActionClick = component::onGenderSelect,
            Modifier.padding(top = 24.dp).fillMaxWidth(),
            expanded = expandedGenders,
            onExpandedChange = { expandedGenders = it },
            placeholder = "Пол",
            activeAction = gender
        )
        val availableRoles by component.availableRoles.collectAsState()
        val role by component.roleField.collectAsState()
        availableRoles?.let { roles ->
            var expandedRoles by remember { mutableStateOf(false) }
            Spinner(
                items = roles,
                onActionClick = component::onRoleSelect,
                Modifier.padding(top = 4.dp).fillMaxWidth(),
                expanded = expandedRoles,
                onExpandedChange = { expandedRoles = it },
                placeholder = "Роль",
                activeAction = role
            )
        }

        val accountFieldsVisibility by component.accountFieldsVisibility.collectAsState()
        if (accountFieldsVisibility) {
            HeaderItem("Вход в аккаунт")
            val email by component.emailField.collectAsState()
            OutlinedTextField(
                value = email,
                onValueChange = component::onEmailType,
                Modifier.fillMaxWidth(),
                label = { TextM2("Почта") },
                leadingIcon = { Icon(painterResource("ic_email".toDrawablePath()), null) },
                singleLine = true,
                isError = errorState.emailMessage != null,
            )
            SupportingText(errorState.emailMessage ?: "", true)

            val password by component.passwordField.collectAsState()
            var passwordVisible by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = password,
                onValueChange = component::onPasswordType,
                Modifier.padding(top = 4.dp).fillMaxWidth(),
                label = { TextM2("Пароль") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                            if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                singleLine = true,
                isError = errorState.passwordMessage != null,
            )
            SupportingText(errorState.passwordMessage ?: "", true)

        }
        val groupVisibility by component.groupFieldVisibility.collectAsState()
        if (groupVisibility) {
            val groupHeader by component.groupField.collectAsState()
            androidx.compose.material.Divider(
                Modifier.padding(top = 24.dp).fillMaxWidth()
            )
            OutlinedTextField(
                value = groupHeader.name,
                onValueChange = component::onPasswordType,
                Modifier.padding(top = 16.dp).fillMaxWidth()
                    .clickable(onClick = component::onGroupClick),
                label = { TextM2("Группа") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    disabledTextColor = LocalContentColor.current.copy(LocalContentAlpha.current),
                    disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(ContentAlpha.medium)
                ),
                trailingIcon = {
                    IconButton(onClick = component::onGroupClick) {
                        Icon(imageVector = Icons.Outlined.Link, "Select group")
                    }
                },
                enabled = false,
                singleLine = true,
                isError = errorState.groupMessage != null,
            )
        }
        SupportingText(errorState.groupMessage ?: "", true)

        OutlinedButton(
            onClick = {},
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                .align(Alignment.End),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Удалить пользователя")
        }
        Spacer(Modifier.height(24.dp))
    }
}