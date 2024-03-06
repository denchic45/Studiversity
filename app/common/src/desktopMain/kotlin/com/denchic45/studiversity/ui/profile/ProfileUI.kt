package com.denchic45.studiversity.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.systemRoleName
import com.denchic45.studiversity.ui.CardContent
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.ui.theme.toDrawablePath
import com.seiko.imageloader.rememberAsyncImagePainter


@Composable
fun ProfileScreen(component: ProfileComponent) {
    val state by component.viewState.collectAsState()
    Box(Modifier.widthIn(max = 420.dp)) {
        ProfileContent(state)
    }
}

@Composable
fun ProfileContent(state: Resource<ProfileViewState>) {
    Column {
        CardContent {
            Row(Modifier.height(120.dp), verticalAlignment = Alignment.CenterVertically) {
                state.onSuccess { profile ->
                    val user = profile.user
                    Image(
                        painter = rememberAsyncImagePainter(user.avatarUrl),
                        null,
                        Modifier.size(68.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(MaterialTheme.spacing.normal))
                    Row {
                        Text(user.fullName)
                        Text(profile.role.systemRoleName())

                        if (profile.studyGroups.size == 1) {
                            Row {
                                Icon(Icons.Outlined.Groups, null)
                                Spacer(Modifier.width(MaterialTheme.spacing.small))
                                val studyGroup = profile.studyGroups.single()
                                Text(studyGroup.name)
                            }
                        } else if (profile.studyGroups.size > 1) {
                            // TODO: сделать диалоговое окно с несклькими группами
                            Icon(Icons.Outlined.Groups, null)
                            Text("${profile.studyGroups.size} групп")
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(MaterialTheme.spacing.normal))

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSideBar(
    modifier: Modifier,
    profileComponent: ProfileComponent,
    onCloseClick: () -> Unit,
) {
    val profileViewState by profileComponent.viewState.collectAsState()
    Column(modifier) {
        TopAppBar(
            title = {},
            actions = {
                IconButton(onCloseClick) {
                    Icon(Icons.Rounded.Close, null)
                }
            },
            colors = topAppBarColors(
                containerColor = Color.Transparent,
//        scrolledContainerColor = MaterialTheme.colorScheme.applyTonalElevation(
//    backgroundColor = containerColor,
//    elevation = TopAppBarSmallTokens.OnScrollContainerElevation
//)
            )
        )
        profileViewState.onSuccess { profile ->
            val user = profile.user
            ProfileHeader(user.avatarUrl, user.fullName)
//            if (profile.groupInfo != null) {
//                ListItem(Modifier.clickable(
//                    profile.groupClickable,
//                    onClick = profileComponent::onGroupClick
//                ),
//                    icon = { Icon(painterResource("ic_study_group".toDrawablePath()), null) },
//                    text = {
//                        Text(text = profile.groupInfo, style = MaterialTheme.typography.bodyLarge)
//                    })
//            }
            HorizontalDivider(Modifier.fillMaxWidth())
            user.account.let { personalDate ->
                ListItem(
                    leadingContent = {
                        Icon(painterResource("ic_email".toDrawablePath()), null)
                    },
                    headlineContent = {
                        Text(personalDate.email, style = MaterialTheme.typography.bodyLarge)
                    },
                    supportingContent = { Text("Почта", style = MaterialTheme.typography.bodyMedium) })
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
        Image(
            painter = rememberAsyncImagePainter(photoUrl),
            null,
            Modifier.size(68.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Column(Modifier.padding(start = 16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
        }
    }
}