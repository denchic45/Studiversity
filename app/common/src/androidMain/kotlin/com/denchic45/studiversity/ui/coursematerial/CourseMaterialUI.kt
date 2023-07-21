package com.denchic45.studiversity.ui.coursematerial

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.ui.appbar.AppBarContent
import com.denchic45.studiversity.ui.appbar.DropdownMenuItem2
import com.denchic45.studiversity.ui.appbar.updateAppBarState
import com.denchic45.studiversity.ui.attachment.AttachmentListItem
import com.denchic45.studiversity.ui.component.HeaderItemUI
import com.denchic45.studiversity.ui.model.AttachmentItem
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.studiversity.util.AttachmentViewer
import com.denchic45.studiversity.util.collectWithLifecycle
import com.denchic45.studiversity.util.findActivity
import com.denchic45.stuiversity.api.course.material.model.CourseMaterialResponse

@Composable
fun CourseMaterialScreen(component: CourseMaterialComponent) {
    val materialResource by component.courseMaterial.collectAsState()
    val attachmentsResource by component.attachments.collectAsState()
    val context = LocalContext.current

    val allowEdit by component.allowEditMaterial.collectAsState(initial = false)

    updateAppBarState(
        allowEdit, AppBarContent(
            dropdownItems = if (allowEdit)
                listOf(
                    DropdownMenuItem2(
                        title = uiTextOf("Изменить"),
                        onClick = component::onEditClick
                    ),
                    DropdownMenuItem2(
                        title = uiTextOf("Удалить"),
                        onClick = component::onDeleteClick
                    )
                ) else emptyList()
        )
    )

    val attachmentViewer by lazy {
        AttachmentViewer(context.findActivity()) {
            Toast.makeText(
                context,
                "Невозможно открыть файл на данном устройстве",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    component.openAttachment.collectWithLifecycle {
        attachmentViewer.openAttachment(it)
    }

    val refreshing by component.refreshing.collectAsState()
    val refreshState = rememberPullRefreshState(refreshing, component::onRefresh)

    Box(modifier = Modifier.pullRefresh(refreshState)) {
        CourseMaterialContent(
            materialResource = materialResource,
            attachmentsResource = attachmentsResource,
            onAttachmentClick = component::onAttachmentClick
        )
        PullRefreshIndicator(refreshing, refreshState, Modifier.align(Alignment.TopCenter))
    }
}

@Composable
private fun CourseMaterialContent(
    materialResource: Resource<CourseMaterialResponse>,
    attachmentsResource: Resource<List<AttachmentItem>>,
    onAttachmentClick: (item: AttachmentItem) -> Unit,
) {
    materialResource.onSuccess { material ->
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(bottom = MaterialTheme.spacing.normal)
        ) {
            Column(Modifier.padding(horizontal = MaterialTheme.spacing.normal)) {
                CourseMaterialHeader(material.name)
                material.description?.let {
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.normal))
                    Text(text = it, style = MaterialTheme.typography.bodyLarge)
                }
            }
            attachmentsResource.onSuccess { attachments ->
                if (attachments.isNotEmpty()) {
                    HeaderItemUI(
                        name = "Прикрепленные файлы",
                    )
                    LazyRow(contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.normal)) {
                        items(attachments, key = { it.attachmentId }) {
                            AttachmentListItem(item = it, onClick = { onAttachmentClick(it) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CourseMaterialHeader(name: String) {
    Column {
        Text(text = name, style = MaterialTheme.typography.titleLarge)
    }
}