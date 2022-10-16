package com.denchic45.kts.ui.usereditor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.TextField
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denchic45.kts.ui.components.HeaderItem
import com.denchic45.kts.ui.components.Spinner
import com.denchic45.kts.ui.theme.TextM2
import com.denchic45.kts.ui.theme.toDrawablePath
import com.denchic45.kts.util.AsyncImage
import com.denchic45.kts.util.loadImageBitmap

@Composable
fun UserEditorScreen(component: UserEditorComponent) {
    Column(Modifier.padding(vertical = 48.dp)) {
        AsyncImage(load = { loadImageBitmap("") },
            painterFor = { BitmapPainter(it) },
            null) {
            Box(Modifier.size(40.dp))
        }
        HeaderItem("Личные данные")
        TextField(
            value = "",
            onValueChange = {},
            label = { TextM2("Имя") }
        )
        TextField(
            value = "",
            onValueChange = {},
            Modifier.padding(top = 24.dp),
            label = { TextM2("Фамилия") }
        )
        TextField(
            value = "",
            onValueChange = {},
            Modifier.padding(top = 24.dp),
            label = { TextM2("Отчество") },
            placeholder = { TextM2("Отчество (необязательно)") }
        )

        var expanded by remember { mutableStateOf(false) }
        Spinner(
            items = component.genders,
            onActionClick = { component.onGenderSelect(it) },
            expanded = expanded,
            onExpandedChange = { expanded = it },
            placeholder = "Пол"
        )
        HeaderItem("Вход в аккаунт")
        TextField(
            value = "",
            onValueChange = {},
            Modifier.padding(top = 24.dp),
            label = { TextM2("Фамилия") },
            leadingIcon = { Icon(painterResource("ic_email".toDrawablePath()), null) }
        )
    }
}