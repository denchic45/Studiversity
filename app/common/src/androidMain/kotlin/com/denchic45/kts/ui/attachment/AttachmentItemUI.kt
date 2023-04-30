package com.denchic45.kts.ui.attachment

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.denchic45.kts.data.domain.model.FileState
import com.denchic45.kts.ui.model.AttachmentItem

@Composable
fun AttachmentItemUI(
    item: AttachmentItem,
    onClick: () -> Unit,
    onRemove: (() -> Unit)? = null
) {
    AssistChip(onClick = { onClick() },
        label = {
            Text(
                text = when (item) {
                    is AttachmentItem.FileAttachmentItem -> item.name
                    is AttachmentItem.LinkAttachmentItem -> item.url
                }
            )
        },
        leadingIcon = {
            when (item) {
                is AttachmentItem.FileAttachmentItem -> {
                    when (item.state) {
                        FileState.Downloaded -> Icon(
                            imageVector = Icons.Outlined.AttachFile,
                            contentDescription = "attachment"
                        )

                        FileState.Downloading -> CircularProgressIndicator()

                        FileState.FailDownload -> Icon(
                            imageVector = Icons.Outlined.Error,
                            contentDescription = "download failed"
                        )

                        FileState.Preview -> Icon(
                            imageVector = Icons.Outlined.Download,
                            contentDescription = "download"
                        )
                    }
                }

                is AttachmentItem.LinkAttachmentItem -> {
                    Icon(imageVector = Icons.Outlined.Link, contentDescription = "attachment")
                }
            }
        },
        trailingIcon = {
            onRemove?.let {
                IconButton(onClick = { it() }) {
                    Icon(imageVector = Icons.Outlined.Close, contentDescription = "attachment")
                }
            }
        }
    )
}