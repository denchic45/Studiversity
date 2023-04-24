package com.denchic45.kts.ui.usereditor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.component.HeaderItemUI
import com.denchic45.kts.ui.components.Spinner
import com.denchic45.kts.ui.components.SupportingText
import com.denchic45.kts.ui.components.dialog.AlertDialog
import com.denchic45.kts.ui.onString
import com.denchic45.kts.ui.theme.toDrawablePath
import com.denchic45.kts.util.AsyncImage
import com.denchic45.kts.util.loadImageBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserEditorScreen(
    component: UserEditorComponent,
    appBarInteractor: AppBarInteractor,
    modifier: Modifier = Modifier
) {
    val appBarState by appBarInteractor.stateFlow.collectAsState()
    Column(modifier) {
        TopAppBar(
            title = {
                appBarState.title.onString {
                    Text(it)
                }
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
            UserEditorContent(component)
            component.userId?.let {
                OutlinedButton(
                    onClick = component::onRemoveClick,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        .align(Alignment.End),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Удалить пользователя")
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun UserEditorDialog(
    component: UserEditorComponent,
    appBarInteractor: AppBarInteractor,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        modifier = Modifier.heightIn(max = 648.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
            ) {
                val appBarState by appBarInteractor.stateFlow.collectAsState()
                appBarState.title.onString {
                    Text(it)
                }
                Spacer(Modifier.weight(1f))
                IconButton({ onDismissRequest() }) {
                    Icon(Icons.Rounded.Close, "")
                }
            }
        },
        onDismissRequest = onDismissRequest,
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                UserEditorContent(component)
            }
        },
        confirmButton = { TextButton(component::onSaveClick) { Text("Сохранить") } },
        dismissButton = { TextButton({ onDismissRequest() }) { Text("Отмена") } },
        optionalButton = { TextButton(component::onRemoveClick) { Text("Удалить") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserEditorContent(component: UserEditorComponent) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        val photoUrl by component.avatarUrl.collectAsState()
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
        Box(Modifier.size(148.dp).background(Color.Gray))

        val errorState by component.errorState.collectAsState()

        HeaderItemUI("Личные данные")

        val firstName by component.firstNameField.collectAsState()
        OutlinedTextField(
            value = firstName,
            onValueChange = component::onFirstNameType,
            Modifier.fillMaxWidth(),
            label = { Text("Имя") },
            singleLine = true,
            isError = errorState.firstNameMessage != null,
        )
        SupportingText(errorState.firstNameMessage ?: "", true)
        val surname by component.surnameField.collectAsState()
        OutlinedTextField(
            value = surname,
            onValueChange = component::onSurnameType,
            Modifier.padding(top = 4.dp).fillMaxWidth(),
            label = { Text("Фамилия") },
            singleLine = true,
            isError = errorState.surnameMessage != null,
        )
        SupportingText(errorState.surnameMessage ?: "", true)
        val patronymic by component.patronymicField.collectAsState()
        OutlinedTextField(
            value = patronymic,
            onValueChange = component::onPatronymicType,
            Modifier.padding(top = 4.dp).fillMaxWidth(),
            label = { Text("Отчество") },
            placeholder = { Text("Отчество (необязательно)") },
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

        val accountFieldsVisibility by component.accountFieldsVisibility.collectAsState()
        if (accountFieldsVisibility) {
            HeaderItemUI("Вход в аккаунт")
            val email by component.emailField.collectAsState()
            OutlinedTextField(
                value = email,
                onValueChange = component::onEmailType,
                Modifier.fillMaxWidth(),
                label = { Text("Почта") },
                leadingIcon = { Icon(painterResource("ic_email".toDrawablePath()), null) },
                singleLine = true,
                isError = errorState.emailMessage != null,
            )
            SupportingText(errorState.emailMessage ?: "", true)

//            val password by component.passwordField.collectAsState()
//            var passwordVisible by remember { mutableStateOf(false) }
//            OutlinedTextField(
//                value = password,
//                onValueChange = component::onPasswordType,
//                Modifier.padding(top = 4.dp).fillMaxWidth(),
//                label = { Text("Пароль") },
//                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
//                trailingIcon = {
//                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
//                        Icon(
//                            imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
//                            if (passwordVisible) "Hide password" else "Show password"
//                        )
//                    }
//                },
//                singleLine = true,
//                isError = errorState.passwordMessage != null,
//            )
//            SupportingText(errorState.passwordMessage ?: "", true)

        }

//        val groupVisibility by component.groupFieldVisibility.collectAsState()
//        if (groupVisibility) {
//            val groupHeader by component.groupField.collectAsState()
//            androidx.compose.material.Divider(
//                Modifier.padding(top = 24.dp).fillMaxWidth()
//            )
//            OutlinedTextField(
//                value = groupHeader.name,
//                onValueChange = component::onPasswordType,
//                Modifier.padding(top = 16.dp).fillMaxWidth()
//                    .clickable(onClick = component::onGroupClick),
//                label = { Text("Группа") },
//                colors = TextFieldDefaults.outlinedTextFieldColors(
//                    disabledTextColor = LocalContentColor.current.copy(LocalContentAlpha.current),
//                    disabledLabelColor = LocalContentColor.current.copy(LocalContentAlpha.current),
//                    disabledBorderColor = MaterialTheme.colorScheme.outline
//                ),
//                trailingIcon = {
//                    IconButton(onClick = component::onGroupClick) {
//                        Icon(imageVector = Icons.Outlined.Link, "Select group")
//                    }
//                },
//                enabled = false,
//                singleLine = true,
//                isError = errorState.groupMessage != null,
//            )
//        }
//        SupportingText(errorState.groupMessage ?: "", true)
    }
}