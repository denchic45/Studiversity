package com.denchic45.studiversity.ui.coursework

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.takeValueIfSuccess
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.component.TabIndicator
import com.denchic45.studiversity.ui.theme.LocalBackDispatcher

@Composable
fun CourseWorkScreen(component: CourseWorkComponent) {
    val childrenResource by component.children.collectAsState()

    val backDispatcher = LocalBackDispatcher.current

    val yourSubmissionComponent = component.yourSubmissionComponent
    val submissionResource by yourSubmissionComponent.submission.collectAsState()
    val hasSubmission by yourSubmissionComponent.hasSubmission.collectAsState()

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

    val allowEdit by component.allowEditWork.collectAsState(initial = false)

    LaunchedEffect(Unit) {
        component.openAttachment.collect {
            TODO("Open attachment")
        }
    }

    var selectedTab by remember { mutableStateOf(0) }

    Row(Modifier.widthIn(max = 960.dp)) {
        CourseWorkBody(
            childrenResource = childrenResource,
            allowEdit = allowEdit,
            selectedTab = selectedTab,
            onTabSelect = { selectedTab = it },
            onEditClick = component::onEditClick,
            onDeleteClick = component::onDeleteClick,
            onClose = backDispatcher::back
        )
        if (hasSubmission.takeValueIfSuccess() == true) {
            SubmissionPanel(
                resource = submissionResource,
                onAttachmentAdd = {
                    // TODO: use desktop file chooser
                },
                onAttachmentClick = component::onAttachmentClick,
                onAttachmentRemove = yourSubmissionComponent::onAttachmentRemove,
                onSubmit = yourSubmissionComponent::onSubmit,
                onCancel = yourSubmissionComponent::onCancel
            )
        }

    }

//    CourseWorkContent(
//        modifier = Modifier.widthIn(max = 960.dp),
//        childrenResource = childrenResource,
//        submissionResource = if (hasSubmission.takeValueIfSuccess() == true) submissionResource else null,
//        allowEdit = allowEdit,
//        onEditClick = component::onEditClick,
//        onDeleteClick = component::onDeleteClick,
//        onClose = backDispatcher::back

//        onAttachmentAdd = {
//            chooseMultipleFiles("Выбрать файлы") {
//                yourSubmissionComponent.onFilesSelect(it.map(File::toOkioPath))
//            }
//        },
//        onAttachmentClick = { component.onAttachmentClick(it) },
//        onAttachmentRemove = yourSubmissionComponent::onAttachmentRemove,
//        onSubmit = yourSubmissionComponent::onSubmit,
//        onCancel = yourSubmissionComponent::onCancel,
//        submissionExpanded = submissionExpanded,
//        onSubmissionExpandChange = yourSubmissionComponent::onExpandChanged
//    )
}


@Composable
private fun CourseWorkContent(
    modifier: Modifier = Modifier,
    childrenResource: Resource<List<CourseWorkComponent.Child>>,
    submissionResource: Resource<SubmissionUiState>?,
    allowEdit: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onClose: () -> Unit
) {


//    BottomSheetScaffold(
//        sheetPeekHeight = topHeight.pxToDp(),
//        scaffoldState = scaffoldState,
//        sheetSwipeEnabled = offset != 0F,
//        sheetContent = {
//            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
//                submissionResource?.onSuccess { submission ->
//                    SubmissionHeaderContent(
//                        submission,
//                        Modifier
//                            .onGloballyPositioned { coordinates ->
//                                headerHeight = coordinates.size.height.toFloat()
//                            }
//                            .padding(top = MaterialTheme.spacing.normal)
//                    )
//                    Box(Modifier.fillMaxHeight()) {
//                        Column(Modifier.fillMaxHeight()) {
//                            SubmissionSheetExpanded(
//                                uiState = submission,
//                                modifier = Modifier
//                                    .height(screenHeight.pxToDp() + collapsedHeight.pxToDp())
//                                    .alpha(transition)
//                                    .clickable(enabled = false, onClick = {}),
//                                onAttachmentAdd = onAttachmentAdd,
//                                onAttachmentClick = onAttachmentClick,
//                                onAttachmentRemove = onAttachmentRemove,
//                                onSubmit = onSubmit,
//                                onCancel = onCancel
//                            )
//                        }
//
//                        if (transition < 1F)
//                            SubmissionCollapsedContent(
//                                submission,
//                                Modifier
//                                    .clickable(enabled = false, onClick = {})
//                                    .alpha(1 - transition)
//                                    .onGloballyPositioned { coordinates ->
//                                        collapsedHeight = coordinates.size.height.toFloat()
//                                    },
//                                onAttachmentAdd,
//                                onSubmit,
//                                onCancel
//                            )
//                    }
//                }
//            }
//        },
//        sheetDragHandle = null
//    ) {
//
//    }
}

@Composable
private fun CourseWorkBody(
    childrenResource: Resource<List<CourseWorkComponent.Child>>,
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

    ResourceContent(resource = childrenResource) { children ->
        Column(Modifier.fillMaxSize()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.ArrowBack, "pop")
                }
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
                Divider()
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (val child = children[selectedTab]) {
                    is CourseWorkComponent.Child.Details -> CourseWorkDetailsScreen(
                        component = child.component,
                        allowEdit = allowEdit,
                        onEditClick = onEditClick,
                        onDeleteClick = onDeleteClick
                    )

                    is CourseWorkComponent.Child.Submissions -> {
                        CourseWorkSubmissionsScreen(child.component)
                    }
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