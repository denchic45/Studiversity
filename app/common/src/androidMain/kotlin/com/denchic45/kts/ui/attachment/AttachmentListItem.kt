package com.denchic45.kts.ui.attachment

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.kts.R
import com.denchic45.kts.ui.model.AttachmentItem
import com.denchic45.kts.ui.theme.spacing
import com.seiko.imageloader.rememberAsyncImagePainter

@Composable
fun AttachmentListItem(
    item: AttachmentItem,
    onClick: () -> Unit,
    onRemove: (() -> Unit)? = null
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(),
        elevation = CardDefaults.elevatedCardElevation(0.dp),
        modifier = Modifier
            .size(192.dp)
            .clickable(onClick = onClick)
            .padding(MaterialTheme.spacing.extraSmall)
    ) {
        Column {
            Image(
                painter = item.previewUrl
                    ?.let { rememberAsyncImagePainter(url = it) }
                    ?: rememberAsyncImagePainter(resId = R.drawable.ic_attachment),
                contentDescription = "attachment image",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Row {
                Text(
                    text = when (item) {
                        is AttachmentItem.FileAttachmentItem -> item.name
                        is AttachmentItem.LinkAttachmentItem -> item.url
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
                onRemove?.let {
                    IconButton(onClick = onRemove) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "remove attachment"
                        )
                    }
                }
            }
        }
    }
//    AssistChip(onClick = { onClick() },
//        label = {
//            Text(
//                text = when (item) {
//                    is AttachmentItem.FileAttachmentItem -> item.name
//                    is AttachmentItem.LinkAttachmentItem -> item.url
//                }
//            )
//        },
//        leadingIcon = {
//            when (item) {
//                is AttachmentItem.FileAttachmentItem -> {
//                    when (item.state) {
//                        FileState.Downloaded -> Icon(
//                            imageVector = Icons.Outlined.AttachFile,
//                            contentDescription = "attachment"
//                        )
//
//                        FileState.Downloading -> CircularProgressIndicator()
//
//                        FileState.FailDownload -> Icon(
//                            imageVector = Icons.Outlined.Error,
//                            contentDescription = "download failed"
//                        )
//
//                        FileState.Preview -> Icon(
//                            imageVector = Icons.Outlined.Download,
//                            contentDescription = "download"
//                        )
//                    }
//                }
//
//                is AttachmentItem.LinkAttachmentItem -> {
//                    Icon(imageVector = Icons.Outlined.Link, contentDescription = "attachment")
//                }
//            }
//        },
//        trailingIcon = {
//            onRemove?.let {
//                IconButton(onClick = { it() }) {
//                    Icon(imageVector = Icons.Outlined.Close, contentDescription = "attachment")
//                }
//            }
//        }
//    )
}