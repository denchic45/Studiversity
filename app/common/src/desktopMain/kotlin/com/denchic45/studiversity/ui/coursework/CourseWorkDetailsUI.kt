package com.denchic45.studiversity.ui.coursework

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.coursework.details.CourseWorkDetailsComponent
import com.denchic45.studiversity.ui.model.AttachmentItem
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse


@Composable
fun CourseWorkDetailsScreen(
    component: CourseWorkDetailsComponent,
    allowEdit: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val workResource by component.courseWork.collectAsState()
    val attachmentsResource by component.attachments.collectAsState()

//    val attachmentViewer by lazy {
//        AttachmentViewer(context.findActivity()) {
//            Toast.makeText(
//                context,
//                "Невозможно открыть файл на данном устройстве",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//    }

//    component.openAttachment.collectWithLifecycle {
//        attachmentViewer.openAttachment(it)
//    }

    CourseWorkDetailsContent(
        workResource = workResource,
        attachmentsResource = attachmentsResource,
        onAttachmentClick = component::onAttachmentClick,
        allowEdit = allowEdit,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick
    )
}

@Composable
fun CourseWorkDetailsContent(
    workResource: Resource<CourseWorkResponse>,
    allowEdit: Boolean,
    attachmentsResource: Resource<List<AttachmentItem>>,
    onAttachmentClick: (item: AttachmentItem) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    ResourceContent(workResource) {
        Column {
            CourseWorkHeader(
                name = it.name,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
                dueDate = it.dueDate,
                allowEdit = allowEdit,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick
            )
        }
    }
}

