package com.denchic45.studiversity.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.ui.model.UserItem
import com.seiko.imageloader.rememberAsyncImagePainter
import java.util.UUID

@Composable
fun UserListItem(
    item: UserItem,
    onClick: (id: UUID) -> Unit,
    modifier: Modifier = Modifier,
    actionsVisible: Boolean = true,
    selected: Boolean = false,
    interactionSource: MutableInteractionSource = remember(::MutableInteractionSource),
    actions: (@Composable () -> Unit)? = null,
) {
    item.apply {
        val shape = RoundedCornerShape(16.dp)
        Row(
            modifier = modifier.run {
                if (selected) background(MaterialTheme.colorScheme.secondaryContainer, shape)
                else this
            }
                .clip(shape)
                .height(64.dp)
                .selectable(
                    onClick = { onClick(item.id) },
                    selected = selected,
                    interactionSource = interactionSource,
                    indication = rememberRipple(color = MaterialTheme.colorScheme.secondary)
                ).padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(avatarUrl),
                null,
                Modifier.size(40.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.padding(start = 16.dp)) {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                subtitle?.let { Text(it, style = MaterialTheme.typography.labelMedium) }
            }
            Spacer(Modifier.weight(1f))
            Row(Modifier.padding(vertical = 20.dp)) {
                if (actions != null && actionsVisible) {
                    actions()
                }
            }
        }
    }
}

@Preview
@Composable
fun UserItemPreview() {
    UserListItem(
        item = UserItem(
            id = UUID.randomUUID(),
            firstName = "User",
            surname = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3b/Android_new_logo_2019.svg/2560px-Android_new_logo_2019.svg.png",
            avatarUrl = "Sub"
        ),
        onClick = {},
        actions = {
            Icon(
                painterResource("drawable/ic_subject_history.xml"),
                null
            )
        }
    )
}