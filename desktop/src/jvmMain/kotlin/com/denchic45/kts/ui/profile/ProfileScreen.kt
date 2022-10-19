package com.denchic45.kts.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
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
import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.ui.components.ListItem
import com.denchic45.kts.ui.theme.toDrawablePath
import com.denchic45.kts.util.AsyncImage
import com.denchic45.kts.util.loadImageBitmap
import io.ktor.http.*


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(
    modifier: Modifier,
    profileComponent: ProfileComponent,
    onCloseClick: () -> Unit,
) {
    val profileViewState by profileComponent.profileViewState.collectAsState()
    Column(modifier) {
        SmallTopAppBar(
            title = {},
            actions = {
                IconButton(onCloseClick) {
                    Icon(painterResource("ic_close".toDrawablePath()), null)
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent)
        )
        val photoUrl by profileComponent.photoUrl.collectAsState(null)
        photoUrl?.let {
            Text(it)
            println("ProfileScreen: $it")

            Image(
                painter = BitmapPainter(loadImageBitmap(it)),
                null,
                modifier = Modifier.padding(top = 48.dp).size(148.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

        }

        profileViewState?.let { profile ->
            ProfileHeader(
                profile.photoUrl, profile.fullName, when (profile.role) {
                    UserRole.STUDENT -> "Студент"
                    UserRole.TEACHER -> "Преподаватель"
                    UserRole.HEAD_TEACHER -> "Завуч"
                }
            )
            Divider(Modifier.fillMaxWidth())
            if (profile.groupInfo != null) {
                ListItem(Modifier.clickable(
                    profile.groupClickable,
                    onClick = profileComponent::onGroupClick
                ),
                    icon = { Icon(painterResource("ic_group".toDrawablePath()), null) },
                    text = {
                        Text(text = profile.groupInfo, style = MaterialTheme.typography.bodyLarge)
                    })
            }
            Divider(Modifier.fillMaxWidth())
            profile.personalDate?.let { personalDate ->
                ListItem(icon = {
                    Icon(painterResource("ic_email".toDrawablePath()), null)
                }, text = {
                    Text(personalDate.email, style = MaterialTheme.typography.bodyLarge)
                }, secondaryText = { Text("Почта", style = MaterialTheme.typography.bodyMedium) })
            }
        }
    }
}

@Composable
fun ProfileHeader(photoUrl: String, title: String, subtitle: String) {
    Row(
        modifier = Modifier.height(100.dp).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            load = { loadImageBitmap(photoUrl) },
            painterFor = { BitmapPainter(it) },
            null,
            Modifier.size(68.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        ) {
            Box(Modifier.size(68.dp).background(Color.LightGray))
        }
        Column(Modifier.padding(start = 16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(subtitle, style = MaterialTheme.typography.titleMedium)
        }
    }
}