package com.denchic45.studiversity.ui.admindashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.ui.model.UserItem
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun UsersAdminScreen(component: UsersAdminComponent) {
    AdminSearchScreen(component, UserItem::id) {
        UserListItem(item = it, onClick = { component.onUserClick(it.id) })
    }
}

@Composable
private fun UserListItem(
    item: UserItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    item.apply {
        ListItem(
            headlineContent = {
                Column {
                    Text(title, style = MaterialTheme.typography.bodyLarge)
                    subtitle?.let { Text(it, style = MaterialTheme.typography.labelMedium) }
                }
            },
            leadingContent = {
                KamelImage(
                    resource = asyncPainterResource(avatarUrl),
                    null,
                    Modifier.size(40.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            },
            modifier = modifier
                .height(64.dp)
                .clickable(onClick = onClick)
        )
//        Row(
//            modifier = modifier
//                .clip(RoundedCornerShape(16.dp))
//                .height(64.dp)
//                .clickable(onClick = onClick)
//                .padding(horizontal = 16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            KamelImage(
//                resource = asyncPainterResource(avatarUrl),
//                null,
//                Modifier.size(40.dp).clip(CircleShape),
//                contentScale = ContentScale.Crop
//            )
//            Column(Modifier.padding(start = MaterialTheme.spacing.normal)) {
//                Text(title, style = MaterialTheme.typography.bodyLarge)
//                subtitle?.let { Text(it, style = MaterialTheme.typography.labelMedium) }
//            }
//        }
    }
}