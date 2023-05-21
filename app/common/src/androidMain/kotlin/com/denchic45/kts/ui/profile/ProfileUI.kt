package com.denchic45.kts.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.ui.ResourceContent
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.appbar.AppBarState
import com.denchic45.kts.ui.theme.spacing
import java.util.UUID

@Composable
fun ProfileScreen(component: ProfileComponent, appBarInteractor: AppBarInteractor) {
    appBarInteractor.set(AppBarState())

    val viewState by component.viewState.collectAsState()

    ProfileContent(viewState, component::onStudyGroupClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(viewState: Resource<ProfileViewState>, onStudyGroupClick: (UUID) -> Unit) {
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
                        .clip(CircleShape),
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
        when (profile.studyGroups.size) {
            0 -> {}
            1 -> {
                val studyGroup = profile.studyGroups.first()
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
                Card(
                    onClick = { TODO() },
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
                        Text(profile.studyGroups.size.toString())
                    }
                }
            }
        }

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