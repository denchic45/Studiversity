package com.denchic45.kts.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denchic45.kts.ui.model.UserItem
import com.denchic45.kts.util.AsyncImage
import com.denchic45.kts.util.loadImageBitmap


@Composable
fun HeaderItem(name: String) {
    Row(Modifier.height(48.dp).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Text(name, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun UserListItem(
    modifier: Modifier,
    item: UserItem,
    actionsOnHover: Boolean = false,
    actions: @Composable () -> Unit,
) {
    val interactionSource = remember(::MutableInteractionSource)
    item.apply {
        Row(modifier = modifier.height(64.dp).clip(RoundedCornerShape(16.dp))
            .clickable(onClick = {},
                interactionSource = interactionSource,
                indication = rememberRipple(color = MaterialTheme.colorScheme.secondary))
            .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(load = { loadImageBitmap(photoUrl) },
                painterFor = { BitmapPainter(it) },
                null,
                Modifier.size(40.dp).clip(CircleShape)) { Box(Modifier.size(40.dp)) }
            Column(Modifier.padding(start = 16.dp)) {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                subtitle?.let { Text(it, style = MaterialTheme.typography.labelSmall) }
            }
            Spacer(Modifier.weight(1f))
            Row(Modifier.padding(vertical = 20.dp)) {
                if (actionsOnHover) {
                    val entered by interactionSource.collectIsHoveredAsState()
                    if (entered)
                        actions.invoke()
                } else {
                    actions.invoke()
                }

            }
        }
    }
}

@Preview
@Composable
fun UserItemPreview() {
    val modifier = Modifier
    UserListItem(modifier,
        UserItem("",
            "User",
            "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3b/Android_new_logo_2019.svg/2560px-Android_new_logo_2019.svg.png",
            "Sub"),
        actions = {
            Icon(painterResource("drawable/ic_subject_history.xml"), null)
        })
}