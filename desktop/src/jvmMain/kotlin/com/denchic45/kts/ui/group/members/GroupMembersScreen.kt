package com.denchic45.kts.ui.group.members

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denchic45.kts.ui.components.HeaderItem
import com.denchic45.kts.ui.components.UserListItem
import com.denchic45.kts.ui.group.courses.CourseListItemPreview
import com.denchic45.kts.ui.model.UserItem
import com.denchic45.kts.ui.theme.toDrawablePath
import com.denchic45.kts.util.AsyncImage
import com.denchic45.kts.util.loadImageBitmap

@Composable
fun GroupMembersScreen(groupMembersComponent: GroupMembersComponent) {
    Row {
        MembersList(Modifier.weight(3f))
//        Spacer(Modifier.fillMaxHeight().background(Color.Blue).width(422.dp))
        Profile(Modifier.fillMaxHeight().width(422.dp))
    }
}

@Composable
private fun MemberListItem(userItem: UserItem) {
    UserListItem(modifier = Modifier, item = userItem, actionsOnHover = true) {
        IconButton(onClick = {}) {
            Icon(painterResource("drawable/ic_more_vert.xml"), null)
        }
    }
}

@Composable
fun MembersList(modifier: Modifier = Modifier) {
    val photoUrl =
        "https://firebasestorage.googleapis.com/v0/b/kts-app-2ab1f.appspot.com/o/avatars%2F07674495-0048?alt=media&token=d8a2ba11-a544-47e1-97e7-0db11a6d3646"
    LazyColumn(modifier.padding(horizontal = 24.dp), contentPadding = PaddingValues(top = 8.dp)) {
        item { HeaderItem("Куратор") }

        item { MemberListItem(UserItem("", "Ivan", photoUrl, "Sub")) }

        item { HeaderItem("Студенты") }

        items(10) { MemberListItem(UserItem("", "Ivan", photoUrl, "Sub")) }

        item { CourseListItemPreview() }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Profile(modifier: Modifier) {
    Column(modifier) {
        Row(Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.End) {
            IconButton({}) {
                Icon(painterResource("ic_group".toDrawablePath()), null)
            }
            Spacer(Modifier.size(6.dp))
            IconButton({}) {
                Icon(painterResource("ic_close".toDrawablePath()), null)
            }

        }
        ProfileHeader("", "Иван", "Студент")
        Divider(Modifier.fillMaxWidth())
        ListItem(icon = {
            Icon(painterResource("ic_group".toDrawablePath()), null)
        }, text = {
            Text("Участник групы: ПКС-2.2", style = MaterialTheme.typography.bodyLarge)
        })
        Divider(Modifier.fillMaxWidth())
        ListItem(icon = {
            Icon(painterResource("ic_info".toDrawablePath()), null, Modifier.padding(top = 8.dp))
        }, text = {
            Text("+7 (123) 456-78-90", style = MaterialTheme.typography.bodyLarge)
        }, secondaryText = { Text("Телефон", style = MaterialTheme.typography.bodyMedium) })
//        Divider(Modifier.fillMaxWidth())
        ListItem(icon = {
            Box(Modifier.size(24.dp).padding(top = 8.dp))
        }, text = {
            Text("example@mail.ru", style = MaterialTheme.typography.bodyLarge)
        }, secondaryText = { Text("Пароль", style = MaterialTheme.typography.bodyMedium) })
    }
}

@Composable
fun ProfileHeader(photoUrl: String, title: String, subtitle: String?) {
    Row(modifier = Modifier.height(100.dp).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(load = { loadImageBitmap(photoUrl) },
            painterFor = { BitmapPainter(it) },
            null,
            Modifier.size(68.dp).clip(CircleShape)) {
            Box(Modifier.size(68.dp).background(Color.LightGray))
        }
        Column(Modifier.padding(start = 16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            subtitle?.let { Text(it, style = MaterialTheme.typography.titleMedium) }
        }
    }
}


@Preview
@Composable
fun ProfilePreview() {
    Profile(Modifier.fillMaxHeight().width(422.dp))
}
