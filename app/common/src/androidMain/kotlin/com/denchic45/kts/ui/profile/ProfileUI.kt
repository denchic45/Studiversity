package com.denchic45.kts.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.ExpandableMenu
import com.denchic45.kts.ui.ResourceContent
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.appbar.AppBarState
import com.denchic45.kts.ui.chooser.StudyGroupListItem
import com.denchic45.kts.ui.theme.spacing
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(component: ProfileComponent, appBarInteractor: AppBarInteractor) {
    appBarInteractor.set(AppBarState())

    val viewStateResource by component.viewState.collectAsState()
    val childOverlay by component.childOverlay.subscribeAsState()

    ProfileContent(viewStateResource, component::onAvatarClick, component::onStudyGroupClick)

    viewStateResource.onSuccess { viewState ->
        childOverlay.overlay?.let {
            when (it.instance) {
                ProfileComponent.OverlayChild.AvatarDialog -> {
                    AlertDialog(onDismissRequest = component::onDialogClose) {
                        Column {
                            ListItem(
                                modifier = Modifier.clickable(onClick = component::onOpenAvatarClick),
                                headlineContent = { Text("Открыть фото") },
                                leadingContent = {
                                    Icon(Icons.Outlined.AccountCircle, "view photo")
                                }
                            )
                            ListItem(
                                modifier = Modifier.clickable(onClick = component::onUpdateAvatarClick),
                                headlineContent = { Text("Изменить фото") },
                                leadingContent = { Icon(Icons.Outlined.Edit, "update photo") }
                            )
                            ListItem(
                                modifier = Modifier.clickable(onClick = component::onRemoveAvatarClick),
                                headlineContent = { Text("Удалить фото") },
                                leadingContent = { Icon(Icons.Outlined.Delete, "delete photo") }
                            )
                        }
                    }
                }

                ProfileComponent.OverlayChild.FullAvatar -> FullAvatarScreen(
                    url = viewState.avatarUrl,
                    appBarInteractor = appBarInteractor,
                    allowUpdateAvatar = viewState.allowUpdateAvatar
                ) {

                }

                ProfileComponent.OverlayChild.ImageChoose -> TODO()
            }
        }
    }
}

@Composable
fun FullAvatarScreen(
    url: String,
    appBarInteractor: AppBarInteractor,
    allowUpdateAvatar: Boolean,
    onDeleteClick: () -> Unit
) {
    appBarInteractor.set(AppBarState(
        actionsUI = {
            if (allowUpdateAvatar)
                ExpandableMenu {
                    DropdownMenuItem(text = { Text(text = "Удалить") }, onClick = onDeleteClick)
                }
        }
    ))
    AsyncImage(
        model = url,
        contentDescription = "full avatar",
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun ProfileContent(
    viewState: Resource<ProfileViewState>,
    onAvatarClick: () -> Unit,
    onStudyGroupClick: (UUID) -> Unit
) {
    ResourceContent(resource = viewState) { profile ->
        Column(
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = profile.avatarUrl,
                    contentDescription = "user avatar",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onAvatarClick),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(MaterialTheme.spacing.large))
                Text(profile.fullName, style = MaterialTheme.typography.titleMedium)
            }
        }
        Divider(
            Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.small)
        )
        ProfileStudyGroups(profile.studyGroups, onStudyGroupClick)

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

        profile.personalDate?.let { personalDate ->
            ListItem(
                leadingContent = {
                    Icon(Icons.Outlined.Email, null)
                },
                headlineContent = {
                    Text(personalDate.email, style = MaterialTheme.typography.bodyLarge)
                },
                supportingContent = { Text("Почта", style = MaterialTheme.typography.bodyMedium) })
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ProfileStudyGroups(
    studyGroups: List<StudyGroupResponse>,
    onStudyGroupClick: (UUID) -> Unit
) {
    when (studyGroups.size) {
        0 -> {}
        1 -> {
            val studyGroup = studyGroups.first()
            Card(
                onClick = { onStudyGroupClick(studyGroup.id) },
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.normal),
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spacing.normal),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Group, "group")
                    Spacer(Modifier.width(MaterialTheme.spacing.normal))
                    Text("Состоит в группе ${studyGroup.name}")
                }
            }
        }

        else -> {
            var showStudyGroups by remember { mutableStateOf(false) }
            Card(
                onClick = { showStudyGroups = true },
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.normal)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spacing.normal),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Group, "group")
                    Spacer(Modifier.width(MaterialTheme.spacing.normal))
                    Text("Группы")
                    Spacer(Modifier.weight(1f))
                    Text(studyGroups.size.toString())
                }
            }

            AlertDialog(onDismissRequest = { showStudyGroups = false }) {
                studyGroups.forEach {
                    StudyGroupListItem(
                        response = it,
                        modifier = Modifier.clickable { onStudyGroupClick(it.id) }
                    )
                }
            }
        }
    }
}