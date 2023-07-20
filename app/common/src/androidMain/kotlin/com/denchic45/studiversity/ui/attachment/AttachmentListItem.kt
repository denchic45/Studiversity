package com.denchic45.studiversity.ui.attachment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.domain.model.FileState
import com.denchic45.studiversity.ui.model.AttachmentItem
import com.denchic45.studiversity.ui.theme.spacing
import com.seiko.imageloader.rememberAsyncImagePainter

@Composable
fun AttachmentListItem(
    item: AttachmentItem,
    onClick: () -> Unit,
    onRemove: (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .width(172.dp)
            .clickable(onClick = onClick)
            .padding(MaterialTheme.spacing.extraSmall)
    ) {
        Column {
            Card(
                elevation = CardDefaults.elevatedCardElevation(0.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp),
                    contentAlignment = Alignment.Center
                ) {
                    item.previewUrl?.let { url ->
                        Image(
                            painter = rememberAsyncImagePainter(url = url),
                            contentDescription = "attachment image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } ?: when(item) {
                        is AttachmentItem.FileAttachmentItem -> Icon(
                            imageVector = Icons.Outlined.Attachment,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            contentDescription = "attachment"
                        )
                        is AttachmentItem.LinkAttachmentItem -> Icon(
                            imageVector = Icons.Outlined.Link,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            contentDescription = "attachment"
                        )
                    }

                    if (item is AttachmentItem.FileAttachmentItem) {
                        when (item.state) {
                            FileState.Preview ->
                                AttachmentButton(onClick = onClick) {
                                    Icon(
                                        imageVector = Icons.Outlined.Download,
                                        contentDescription = "attachment button",
                                        tint = Color.White
                                    )
                                }

                            FileState.Downloading -> AttachmentButton(onClick = onClick) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.padding(8.dp),
                                    strokeWidth = 2.dp
                                )
                            }

                            FileState.FailDownload -> AttachmentButton(onClick = onClick) {
                                Icon(
                                    imageVector = Icons.Outlined.Restore,
                                    contentDescription = "attachment button",
                                    tint = Color.White
                                )
                            }

                            FileState.Downloaded -> {}

                        }
                    }
                }
            }

            Row(
                modifier = Modifier.height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
                Text(
                    text = when (item) {
                        is AttachmentItem.FileAttachmentItem -> item.name
                        is AttachmentItem.LinkAttachmentItem -> item.url
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
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

@Composable
fun AttachmentButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}