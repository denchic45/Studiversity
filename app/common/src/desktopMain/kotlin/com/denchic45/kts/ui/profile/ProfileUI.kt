package com.denchic45.kts.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.components.AsyncImage
import com.denchic45.kts.ui.components.loadImageBitmap
import com.denchic45.kts.ui.theme.toDrawablePath


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ProfileSideBar(
    modifier: Modifier,
    profileComponent: ProfileComponent,
    onCloseClick: () -> Unit,
) {
    val profileViewState by profileComponent.profileViewState.collectAsState()
    Column(modifier) {
        TopAppBar(
            title = {},
            actions = {
                IconButton(onCloseClick) {
                    Icon(painterResource("ic_close".toDrawablePath()), null)
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent)
        )
        profileViewState.onSuccess { profile ->
            ProfileHeader(profile.avatarUrl, profile.fullName)
            Divider(Modifier.fillMaxWidth())
//            if (profile.groupInfo != null) {
//                ListItem(Modifier.clickable(
//                    profile.groupClickable,
//                    onClick = profileComponent::onGroupClick
//                ),
//                    icon = { Icon(painterResource("ic_group".toDrawablePath()), null) },
//                    text = {
//                        Text(text = profile.groupInfo, style = MaterialTheme.typography.bodyLarge)
//                    })
//            }
            Divider(Modifier.fillMaxWidth())
            profile.personalDate?.let { personalDate ->
                ListItem(
                    leadingContent = {
                        Icon(painterResource("ic_email".toDrawablePath()), null)
                    },
                    headlineText = {
                        Text(personalDate.email, style = MaterialTheme.typography.bodyLarge)
                    },
                    supportingText = { Text("Почта", style = MaterialTheme.typography.bodyMedium) })
            }
        }
    }
}

@Composable
fun ProfileHeader(photoUrl: String, title: String) {
    Row(
        modifier = Modifier.height(100.dp).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            load = { loadImageBitmap(photoUrl) },
            painterFor = { BitmapPainter(it) },
            photoUrl,
            null,
            Modifier.size(68.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        ) {
            Box(Modifier.size(68.dp).background(Color.LightGray))
        }
        Column(Modifier.padding(start = 16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
        }
    }
}