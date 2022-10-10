package com.denchic45.kts.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denchic45.kts.ui.model.UserItem
import com.denchic45.kts.util.AsyncImage
import com.denchic45.kts.util.loadImageBitmap


@Composable
fun HeaderItem(name: String) {
    Row(
        Modifier.height(48.dp).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun UserListItem(
    modifier: Modifier = Modifier,
    onClick: (id: String) -> Unit,
    item: UserItem,
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
            }.clip(shape).height(64.dp).selectable(
                onClick = { onClick(item.id) },
                selected = selected,
                interactionSource = interactionSource,
                indication = rememberRipple(color = MaterialTheme.colorScheme.secondary)
            ).padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                load = { loadImageBitmap(photoUrl) },
                painterFor = { BitmapPainter(it) },
                null,
                Modifier.size(40.dp).clip(CircleShape)
            ) { Box(Modifier.size(40.dp)) }
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
    val modifier = Modifier
    UserListItem(modifier,
        {},
        UserItem(
            "",
            "User",
            "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3b/Android_new_logo_2019.svg/2560px-Android_new_logo_2019.svg.png",
            "Sub"
        ), actions = {
            Icon(painterResource("drawable/ic_subject_history.xml"), null)
        })
}