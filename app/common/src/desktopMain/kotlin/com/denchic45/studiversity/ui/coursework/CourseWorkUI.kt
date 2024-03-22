package com.denchic45.studiversity.ui.coursework

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.resource.takeValueIfSuccess
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.component.TabIndicator
import com.denchic45.studiversity.ui.main.CustomCenteredAppBar
import com.denchic45.studiversity.ui.main.NavigationIconBack
import com.denchic45.studiversity.ui.theme.LocalBackDispatcher
import com.denchic45.studiversity.ui.theme.spacing

@Composable
fun CourseWorkScreen(component: CourseWorkComponent) {
    val childrenResource by component.children.collectAsState()
    val backDispatcher = LocalBackDispatcher.current

    val yourSubmissionComponent by component.yourSubmissionComponent.collectAsState()

//    val submissionResource by yourSubmissionComponent.submission.collectAsState()
//    val hasSubmission by yourSubmissionComponent.hasSubmission.collectAsState()

    var showFilePicker by remember { mutableStateOf(false) }

//    val pickFileLauncher = rememberLauncherForActivityResult(
//        OpenMultipleAnyDocuments()
//    ) { uris ->
//        yourSubmissionComponent.onFilesSelect(uris.map { it.getFile(context) })
//    }

//    FilePicker(showFilePicker) { path ->
//        showFilePicker = false
//        yourSubmissionComponent.onFilesSelect(uris.map { it.getFile(context) })
//    }


    val allowEdit by component.allowEditWork.collectAsState()

//    LaunchedEffect(Unit) {
//        component.openAttachment.collect {
//            TODO("Open attachment")
//        }
//    }

    var selectedTab by remember { mutableStateOf(0) }

    Row(Modifier.widthIn(max = 960.dp)) {
        ResourceContent(childrenResource) { children ->
            CourseWorkLayout(
                children = children,
                submissionContent = {
                    yourSubmissionComponent.onSuccess { component ->
                        component?.let { YourSubmissionBlock(it) }
                    }
//                    if (hasSubmission.takeValueIfSuccess() == true) {
//                        SubmissionPanel(
//                            resource = submissionResource,
//                            onAttachmentAdd = {
//                                // TODO: use desktop file chooser
//                            },
//                            onAttachmentClick = component::onAttachmentClick,
//                            onAttachmentRemove = yourSubmissionComponent::onAttachmentRemove,
//                            onSubmit = yourSubmissionComponent::onSubmit,
//                            onCancel = yourSubmissionComponent::onCancel
//                        )
//                    }
                },
                allowEdit = allowEdit.takeValueIfSuccess() == true,
                selectedTab = selectedTab,
                onTabSelect = { selectedTab = it },
                onEditClick = component::onEditClick,
                onDeleteClick = component::onDeleteClick,
                onClose = backDispatcher::back
            )
        }
    }
}

@Composable
private fun CourseWorkLayout(
    children: List<CourseWorkComponent.Child>,
    submissionContent: @Composable () -> Unit,
    allowEdit: Boolean,
    selectedTab: Int,
    onTabSelect: (Int) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onClose: () -> Unit,
) {
    LaunchedEffect(selectedTab) {
        // Collect from the pager state a snapshotFlow reading the currentPage
        snapshotFlow { selectedTab }.collect { page ->
            onTabSelect(page)
        }
    }

    Column(Modifier.fillMaxSize()) {
        CustomCenteredAppBar(navigationContent = { NavigationIconBack(onClose) }) {
            if (children.size != 1) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    indicator = { tabPositions ->
                        TabIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedTab]))
                    },
                    divider = {}
                ) {
                    // Add tabs for all of our pages
                    children.forEachIndexed { index, child ->
                        Tab(
                            text = { Text(child.title) },
                            selected = selectedTab == index,
                            onClick = { onTabSelect(index) },
                        )
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (val child = children[selectedTab]) {
                is CourseWorkComponent.Child.Details -> Row {
                    CourseWorkDetailsScreen(
                        component = child.component,
                        allowEdit = allowEdit,
                        onEditClick = onEditClick,
                        onDeleteClick = onDeleteClick
                    )
                    Spacer(Modifier.width(MaterialTheme.spacing.normal))
                    submissionContent()
                }

                is CourseWorkComponent.Child.Submissions -> {
                    CourseWorkSubmissionsScreen(child.component)
                }
            }
        }
    }
}

//@Composable
//fun SubmissionCollapsedContent(
//    submission: SubmissionUiState,
//    modifier: Modifier,
//    onAttachmentAdd: () -> Unit,
//    onSubmit: () -> Unit,
//    onCancel: () -> Unit,
//) {
//    Column(
//        modifier = modifier
//            .padding(horizontal = MaterialTheme.spacing.normal)
//    ) {
//        Row {
//            AssistChip(onClick = { /*TODO*/ },
//                leadingIcon = {
//                    Icon(
//                        imageVector = Icons.Outlined.Comment,
//                        contentDescription = "comments"
//                    )
//                },
//                label = { Text(text = "0") })
//        }
//        if (submission.grade == null)
//            when (submission.state) {
//                SubmissionState.NEW,
//                SubmissionState.CREATED,
//                SubmissionState.CANCELED_BY_AUTHOR,
//                -> {
//                    Button(
//                        onClick = {
//                            if (submission.attachments.isEmpty()) onAttachmentAdd()
//                            else onSubmit()
//                        }, modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Text(text = if (submission.attachments.isEmpty()) "Добавить" else "Сдать")
//                    }
//                }
//
//                SubmissionState.SUBMITTED -> {
//                    FilledTonalButton(
//                        onClick = { onCancel() },
//                        modifier = Modifier.fillMaxWidth(),
//                        colors = ButtonDefaults.filledTonalButtonColors(
//                            containerColor = MaterialTheme.colorScheme.errorContainer,
//                            contentColor = MaterialTheme.colorScheme.error
//                        )
//                    ) {
//                        Text(text = "Отменить отправку")
//                    }
//                }
//            }
//        Spacer(Modifier.height(MaterialTheme.spacing.normal))
//    }
//}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SubmissionSheetCollapsed(
//    submission: SubmissionUiState,
//    modifier: Modifier,
//    onAttachmentAdd: () -> Unit,
//    onSubmit: () -> Unit,
//    onCancel: () -> Unit,
//) {
//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(MaterialTheme.spacing.normal)
//    ) {
//        Row {
//            Column(Modifier.weight(1f)) {
//                val updatedAt = submission.updatedAt?.toString("dd MMM HH:mm")
//                val title = submission.grade?.let {
//                    "Оценено: ${it.value}/5"
//                } ?: when (submission.state) {
//                    SubmissionState.NEW,
//                    SubmissionState.CREATED,
//                    -> "Не сдано"
//
//                    SubmissionState.SUBMITTED -> "Сдано"
//                    SubmissionState.CANCELED_BY_AUTHOR -> "Отменено автором"
//                }
//                Text(text = title, style = MaterialTheme.typography.titleLarge)
//                updatedAt?.let {
//                    Text(text = it, style = MaterialTheme.typography.bodyMedium)
//                }
//            }
//            if (submission.grade == null)
//                when (submission.state) {
//                    SubmissionState.NEW,
//                    SubmissionState.CREATED,
//                    SubmissionState.CANCELED_BY_AUTHOR,
//                    -> {
//                        Button(onClick = {
//                            if (submission.attachments.isEmpty()) onAttachmentAdd()
//                            else onSubmit()
//                        }) {
//                            Text(text = if (submission.attachments.isEmpty()) "Добавить" else "Сдать")
//                        }
//                    }
//
//                    SubmissionState.SUBMITTED -> Button(onClick = { onCancel() }) {
//                        Text(text = "Отменить")
//                    }
//
//                }
//        }
////        Spacer(Modifier.height(MaterialTheme.spacing.normal))
//        AssistChip(onClick = { /*TODO*/ },
//            leadingIcon = {
//                Icon(
//                    imageVector = Icons.Outlined.Comment,
//                    contentDescription = "comments"
//                )
//            },
//            label = { Text(text = "0") })
//    }
//}

//@Composable
//fun SubmissionHeaderContent(
//    submission: SubmissionUiState,
//    modifier: Modifier = Modifier,
//) {
//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(horizontal = MaterialTheme.spacing.normal)
//    ) {
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Column(Modifier.weight(1f)) {
//                val updatedAt = submission.updatedAt?.toString("dd MMM HH:mm")
//                val title = submission.grade?.let {
//                    "Оценено: ${it.value}/5"
//                } ?: when (submission.state) {
//                    SubmissionState.NEW,
//                    SubmissionState.CREATED,
//                    -> "Не сдано"
//
//                    SubmissionState.SUBMITTED -> "Сдано"
//                    SubmissionState.CANCELED_BY_AUTHOR -> "Отменено"
//                }
//                Text(text = title, style = MaterialTheme.typography.titleLarge)
//                updatedAt?.let {
//                    Text(text = it, style = MaterialTheme.typography.bodyMedium)
//                }
//            }
//        }
//    }
//}

//@Composable
//fun SubmissionSheetExpanded(
//    uiState: SubmissionUiState,
//    modifier: Modifier,
//    onAttachmentClick: (AttachmentItem) -> Unit,
//    onAttachmentAdd: () -> Unit,
//    onAttachmentRemove: (attachmentId: UUID) -> Unit,
//    onSubmit: () -> Unit,
//    onCancel: () -> Unit,
//) {
//    Column(
//        modifier.fillMaxWidth()
//    ) {
//        SubmissionDetailsContent(uiState, onAttachmentClick, onAttachmentRemove)
//        Spacer(modifier = Modifier.weight(1f))
//        Row(Modifier.padding(MaterialTheme.spacing.normal)) {
//            when (uiState.state) {
//                SubmissionState.NEW,
//                SubmissionState.CREATED,
//                SubmissionState.CANCELED_BY_AUTHOR,
//                -> {
//                    OutlinedButton(
//                        onClick = { onAttachmentAdd() },
//                        modifier = Modifier.weight(2f),
//                    ) {
//                        Text(text = "Прикрепить файл")
//                    }
//                    if (uiState.attachments.isNotEmpty()) {
//                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.normal))
//                        Button(onClick = { onSubmit() }, modifier = Modifier) {
//                            Text(text = "Сдать работу")
//                        }
//                    }
//                }
//
//                SubmissionState.SUBMITTED -> FilledTonalButton(
//                    onClick = { onCancel() },
//                    modifier = Modifier.fillMaxWidth(),
//                    colors = ButtonDefaults.filledTonalButtonColors(
//                        containerColor = MaterialTheme.colorScheme.errorContainer,
//                        contentColor = MaterialTheme.colorScheme.error
//                    )
//                ) {
//                    Text(text = "Отменить отправку")
//                }
//            }
//        }
//    }
//}