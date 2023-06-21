package com.denchic45.studiversity.ui.coursework

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.ui.ExpandableDropdownMenu
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.component.TabIndicator
import com.denchic45.studiversity.ui.theme.LocalBackDispatcher
import com.denchic45.stuiversity.util.DateTimePatterns
import com.denchic45.stuiversity.util.toString
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun CourseWorkScreen(component: CourseWorkComponent) {
    val childrenResource by component.children.collectAsState()

    val backDispatcher = LocalBackDispatcher.current

    val yourSubmissionComponent = component.yourSubmissionComponent
    val submissionResource by yourSubmissionComponent.submission.collectAsState(null)
    val submissionExpanded by yourSubmissionComponent.sheetExpanded.collectAsState()

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

    CourseWorkContent(
        childrenResource = childrenResource,
        submissionResource = submissionResource,
        allowEdit = allowEdit,
        onEditClick = component::onEditClick,
        onDeleteClick = component::onDeleteClick,
        onClose = backDispatcher::back
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
    )
}

@Composable
fun CourseWorkHeader(
    name: String,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime?,
    dueDate: LocalDate?,
    allowEdit: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = name, style = MaterialTheme.typography.headlineMedium)
            if (allowEdit) {
                Spacer(Modifier.weight(1f))
                var expanded by remember { mutableStateOf(false) }
                ExpandableDropdownMenu(expanded, onExpandedChange = { expanded = it }) {
                    DropdownMenuItem(
                        text = { Text("Изменить") },
                        onClick = onEditClick
                    )
                    DropdownMenuItem(
                        text = { Text("Удалить") },
                        onClick = onDeleteClick
                    )
                }
            }
        }
        Row(
            Modifier.height(84.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                createdAt.toString(DateTimePatterns.dd_MMM) +
                        updatedAt?.let { "(Обновлено: ${it.toString(DateTimePatterns.dd_MMM)})" }
            )

            dueDate?.let {
                Spacer(Modifier.weight(1f))
                Text("Срок сдачи: ${it.toString(DateTimePatterns.dd_MMM)}")
            }
        }
    }
}

@Composable
private fun CourseWorkContent(
    childrenResource: Resource<List<CourseWorkComponent.Child>>,
    submissionResource: Resource<SubmissionUiState>?,
    allowEdit: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onClose: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    CourseWorkBody(
        childrenResource = childrenResource,
        allowEdit = allowEdit,
        selectedTab = selectedTab,
        onPageSelect = { selectedTab = it },
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
        onClose = onClose
    )

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
@OptIn(ExperimentalFoundationApi::class)
private fun CourseWorkBody(
    childrenResource: Resource<List<CourseWorkComponent.Child>>,
    allowEdit: Boolean,
    selectedTab: Int,
    onPageSelect: (Int) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onClose: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    LaunchedEffect(pagerState) {
        // Collect from the pager state a snapshotFlow reading the currentPage
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onPageSelect(page)
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
                            TabIndicator(
                                Modifier.tabIndicatorOffset(
                                    tabPositions[selectedTab]
                                )
                            )
                        },
                        divider = {}
                    ) {
                        // Add tabs for all of our pages
                        children.forEachIndexed { index, child ->
                            Tab(
                                text = { Text(child.title) },
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
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


@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }


@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

@Composable
fun Float.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

fun Modifier.gesturesDisabled(disabled: Boolean = true) = if (disabled) {
    pointerInput(Unit) {
        awaitPointerEventScope {
            // we should wait for all new pointer events
            while (true) {
                awaitPointerEvent(pass = PointerEventPass.Initial)
                    .changes
                    .forEach(PointerInputChange::consume)
            }
        }
    }
} else {
    this
}