package com.denchic45.kts.ui.profile

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.ui.theme.toDrawablePath
import com.denchic45.kts.util.AsyncImage
import com.denchic45.kts.util.loadImageBitmap


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(modifier: Modifier, profileComponent: ProfileComponent) {
    val profileViewState by profileComponent.profileViewState.collectAsState()
    profileViewState?.let { profile ->
        Column(modifier) {
            Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.End) {
                IconButton({}) {
                    Icon(painterResource("ic_close".toDrawablePath()), null)
                }
            }
            ProfileHeader(
                profile.photoUrl, profile.fullName, when (profile.role) {
                    UserRole.STUDENT -> "Студент"
                    UserRole.TEACHER -> "Преподаватель"
                    UserRole.HEAD_TEACHER -> "Завуч"
                }
            )
            Divider(Modifier.fillMaxWidth())
            if (profile.groupName != null) {
                ListItem(icon = { Icon(painterResource("ic_group".toDrawablePath()), null) },
                    text = {
                        Text(
                            text = when (profile.role) {
                                UserRole.STUDENT -> "Участник групы: ${profile.groupName}"
                                UserRole.TEACHER, UserRole.HEAD_TEACHER -> {
                                    "Преподаватель групы: ${profile.groupName}"
                                }
                            }, style = MaterialTheme.typography.bodyLarge
                        )
                    })
            }
            Divider(Modifier.fillMaxWidth())
            ListItem(icon = {
                Icon(
                    painterResource("ic_info".toDrawablePath()),
                    null,
                    Modifier.padding(top = 8.dp)
                )
            },
                text = { Text("+7 (123) 456-78-90", style = MaterialTheme.typography.bodyLarge) },
                secondaryText = { Text("Телефон", style = MaterialTheme.typography.bodyMedium) })
//        Divider(Modifier.fillMaxWidth())
            ListItem(icon = {
                Box(Modifier.size(24.dp).padding(top = 8.dp))
            }, text = {
                Text("example@mail.ru", style = MaterialTheme.typography.bodyLarge)
            }, secondaryText = { Text("Пароль", style = MaterialTheme.typography.bodyMedium) })
        }
    }
}

@Composable
fun ProfileHeader(photoUrl: String, title: String, subtitle: String) {
    Row(
        modifier = Modifier.height(100.dp).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(load = { loadImageBitmap(photoUrl) },
            painterFor = { BitmapPainter(it) },
            null,
            Modifier.size(68.dp).clip(CircleShape)
        ) {
            Box(Modifier.size(68.dp).background(Color.LightGray))
        }
        Column(Modifier.padding(start = 16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(subtitle, style = MaterialTheme.typography.titleMedium)
        }
    }
}