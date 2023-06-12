package com.denchic45.studiversity.ui.coursework

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.denchic45.studiversity.data.domain.model.FileState
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.ui.appbar2.AppBarContent
import com.denchic45.studiversity.ui.appbar2.DropdownMenuItem2
import com.denchic45.studiversity.ui.appbar2.updateAppBarState
import com.denchic45.studiversity.ui.coursework.details.CourseWorkDetailsScreen
import com.denchic45.studiversity.ui.coursework.submissiondetails.SubmissionDetailsContent
import com.denchic45.studiversity.ui.coursework.submissions.CourseWorkSubmissionsScreen
import com.denchic45.studiversity.ui.model.AttachmentItem
import com.denchic45.studiversity.ui.theme.AppTheme
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.studiversity.util.AttachmentViewer
import com.denchic45.studiversity.util.OpenMultipleAnyDocuments
import com.denchic45.studiversity.util.collectWithLifecycle
import com.denchic45.studiversity.util.findActivity
import com.denchic45.studiversity.util.getFile
import com.denchic45.stuiversity.api.course.work.submission.model.Author
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionState
import com.denchic45.stuiversity.util.toString
import kotlinx.coroutines.launch
import okio.Path.Companion.toPath
import java.util.UUID

@Composable
fun CourseWorkScreen(component: CourseWorkComponent) {
    val childrenResource by component.children.collectAsState()

    val yourSubmissionComponent = component.yourSubmissionComponent
    val submissionResource by yourSubmissionComponent.uiState.collectAsState(null)
    val submissionExpanded by yourSubmissionComponent.sheetExpanded.collectAsState()

    val context = LocalContext.current

    val pickFileLauncher = rememberLauncherForActivityResult(
        OpenMultipleAnyDocuments()
    ) { uris ->
        yourSubmissionComponent.onFilesSelect(uris.map { it.getFile(context) })
    }

    val attachmentViewer by lazy {
        AttachmentViewer(context.findActivity()) {
            Toast.makeText(
                context,
                "Невозможно открыть файл на данном устройстве",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val allowEdit by component.allowEditWork.collectAsState(initial = false)
//    val appBarState = LocalAppBarState.current

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
                        onClick = component::onRemoveClick
                    )
                ) else emptyList()
        )
    )

//    component.lifecycle.doOnStart {
//        appBarState.content = AppBarContent(dropdownItems = if (allowEdit)
//            listOf(
//                DropdownMenuItem2(
//                    title = uiTextOf("Изменить"),
//                    onClick = { component.onEditClick() }
//                ),
//                DropdownMenuItem2(
//                    title = uiTextOf("Удалить"),
//                    onClick = { component.onRemoveClick() }
//                )
//            ) else emptyList())
//    }

    component.openAttachment.collectWithLifecycle {
        attachmentViewer.openAttachment(it)
    }

    CourseWorkContent(
        childrenResource = childrenResource,
        submissionResource = submissionResource,
        onAttachmentAdd = { pickFileLauncher.launch(Unit) },
        onAttachmentClick = { component.onAttachmentClick(it) },
        onAttachmentRemove = yourSubmissionComponent::onAttachmentRemove,
        onSubmit = yourSubmissionComponent::onSubmit,
        onCancel = yourSubmissionComponent::onCancel,
        submissionExpanded = submissionExpanded,
        onSubmissionExpandChange = yourSubmissionComponent::onExpandChanged
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CourseWorkContent(
    childrenResource: Resource<List<CourseWorkComponent.Child>>,
    submissionResource: Resource<SubmissionUiState>?,
    onAttachmentAdd: () -> Unit,
    onAttachmentClick: (AttachmentItem) -> Unit,
    onAttachmentRemove: (attachmentId: UUID) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    submissionExpanded: Boolean,
    onSubmissionExpandChange: (Boolean) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    var headerHeight by remember { mutableStateOf(0F) }
    var collapsedHeight by remember { mutableStateOf(0F) }

    val topHeight = headerHeight + collapsedHeight

    var offset by remember { mutableStateOf(0F) }
    var hidden by remember { mutableStateOf(false) }
    val bottomSheetState = rememberStandardBottomSheetState(
        skipHiddenState = false,
        confirmValueChange = { it != SheetValue.Hidden }
    )


    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)

    var screenHeight by remember {
        mutableStateOf(0F)
    }
    val expandedOrScreen = screenHeight
    val difference = if (screenHeight != 0F) {
        (expandedOrScreen - offset) / expandedOrScreen
    } else 0F

    val transition = calcPercentOf(0.2F, 0.8F, maxOf(0.2F, minOf(difference, 0.8F)))

    LaunchedEffect(submissionExpanded != bottomSheetState.hasExpandedState) {
        when (submissionExpanded) {
            true -> coroutineScope.launch { bottomSheetState.expand() }
            false -> coroutineScope.launch { bottomSheetState.partialExpand() }
        }
    }

    if (screenHeight != 0f) {
        LaunchedEffect(Unit) {
            snapshotFlow(bottomSheetState::requireOffset)
                .collect {
                    offset = it
                    if (hidden) bottomSheetState.hide()
                }
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { bottomSheetState.currentValue }.collect { value ->
            onSubmissionExpandChange(value == SheetValue.Expanded)
        }
    }

    BottomSheetScaffold(
        sheetPeekHeight = topHeight.pxToDp(),
        scaffoldState = scaffoldState,
        sheetSwipeEnabled = offset != 0F,
        sheetContent = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                submissionResource?.onSuccess { submission ->
                    SubmissionHeaderContent(
                        submission,
                        Modifier
                            .onGloballyPositioned { coordinates ->
                                headerHeight = coordinates.size.height.toFloat()
                            }
                            .padding(top = MaterialTheme.spacing.normal)
                    )
                    Box(Modifier.fillMaxHeight()) {
                        Column(Modifier.fillMaxHeight()) {
                            SubmissionSheetExpanded(
                                uiState = submission,
                                modifier = Modifier
                                    .height(screenHeight.pxToDp() + collapsedHeight.pxToDp())
                                    .alpha(transition)
                                    .clickable(enabled = false, onClick = {}),
                                onAttachmentAdd = onAttachmentAdd,
                                onAttachmentClick = onAttachmentClick,
                                onAttachmentRemove = onAttachmentRemove,
                                onSubmit = onSubmit,
                                onCancel = onCancel
                            )
                        }

                        if (transition < 1F)
                            SubmissionCollapsedContent(
                                submission,
                                Modifier
                                    .clickable(enabled = false, onClick = {})
                                    .alpha(1 - transition)
                                    .onGloballyPositioned { coordinates ->
                                        collapsedHeight = coordinates.size.height.toFloat()
                                    },
                                onAttachmentAdd,
                                onSubmit,
                                onCancel
                            )
                    }
                }
            }
        },
        sheetDragHandle = null
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .padding(it)
                .onGloballyPositioned { coordinates ->
                    screenHeight = coordinates.size.height.toFloat()
                }) {
//            Column {
//                Spacer(modifier = Modifier.height(100.dp))
//                Text("offset ${offset.pxToDp()}")
//                Text("screen height ${screenHeight.pxToDp()}")
//                Text("collapsed height ${collapsedHeight.pxToDp()}")
//                Text("header height ${headerHeight.pxToDp()}")
//                Text("top height ${topHeight.pxToDp()}")
//                Text("difference $difference")
//                Text("transition: $transition")
//
//        println("offset: $offset")
//        println("difference: $difference")
//        println("transition: $transition")
//            }
            CourseWorkBody(childrenResource) {
                coroutineScope.launch {
                    hidden = it != 0
                    if (it == 0)
                        bottomSheetState.partialExpand()
                    else bottomSheetState.hide()
                }
            }
        }
    }
}

fun calcPercentOf(min: Float, max: Float, input: Float): Float {
    return ((input - min) * 1) / (max - min)
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun CourseWorkBody(
    childrenResource: Resource<List<CourseWorkComponent.Child>>,
    onPageSelect: (Int) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    LaunchedEffect(pagerState) {
        // Collect from the pager state a snapshotFlow reading the currentPage
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onPageSelect(page)
        }
    }

    childrenResource.onSuccess { children ->
        Column {
            if (children.size != 1) {
                TabRow(
                    selectedTabIndex = pagerState.currentPage
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
            HorizontalPager(state = pagerState, pageCount = children.size) {
                Box(modifier = Modifier.fillMaxSize()) {
                    when (val child = children[it]) {
                        is CourseWorkComponent.Child.Details -> CourseWorkDetailsScreen(child.component)
                        is CourseWorkComponent.Child.Submissions -> CourseWorkSubmissionsScreen(
                            child.component
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SubmissionCollapsedContent(
    submission: SubmissionUiState,
    modifier: Modifier,
    onAttachmentAdd: () -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(horizontal = MaterialTheme.spacing.normal)
    ) {
        Row {
            AssistChip(onClick = { /*TODO*/ },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Comment,
                        contentDescription = "comments"
                    )
                },
                label = { Text(text = "0") })
        }
        if (submission.grade == null)
            when (submission.state) {
                SubmissionState.NEW,
                SubmissionState.CREATED,
                SubmissionState.CANCELED_BY_AUTHOR,
                -> {
                    Button(
                        onClick = {
                            if (submission.attachments.isEmpty()) onAttachmentAdd()
                            else onSubmit()
                        }, modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = if (submission.attachments.isEmpty()) "Добавить" else "Сдать")
                    }
                }

                SubmissionState.SUBMITTED -> {
                    FilledTonalButton(
                        onClick = { onCancel() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(text = "Отменить отправку")
                    }
                }
            }
        Spacer(Modifier.height(MaterialTheme.spacing.normal))
    }
}

@Composable
fun SubmissionSheetCollapsed(
    submission: SubmissionUiState,
    modifier: Modifier,
    onAttachmentAdd: () -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.normal)
    ) {
        Row {
            Column(Modifier.weight(1f)) {
                val updatedAt = submission.updatedAt?.toString("dd MMM HH:mm")
                val title = submission.grade?.let {
                    "Оценено: ${it.value}/5"
                } ?: when (submission.state) {
                    SubmissionState.NEW,
                    SubmissionState.CREATED,
                    -> "Не сдано"

                    SubmissionState.SUBMITTED -> "Сдано"
                    SubmissionState.CANCELED_BY_AUTHOR -> "Отменено автором"
                }
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                updatedAt?.let {
                    Text(text = it, style = MaterialTheme.typography.bodyMedium)
                }
            }
            if (submission.grade == null)
                when (submission.state) {
                    SubmissionState.NEW,
                    SubmissionState.CREATED,
                    SubmissionState.CANCELED_BY_AUTHOR,
                    -> {
                        Button(onClick = {
                            if (submission.attachments.isEmpty()) onAttachmentAdd()
                            else onSubmit()
                        }) {
                            Text(text = if (submission.attachments.isEmpty()) "Добавить" else "Сдать")
                        }
                    }

                    SubmissionState.SUBMITTED -> Button(onClick = { onCancel() }) {
                        Text(text = "Отменить")
                    }

                }
        }
//        Spacer(Modifier.height(MaterialTheme.spacing.normal))
        AssistChip(onClick = { /*TODO*/ },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Comment,
                    contentDescription = "comments"
                )
            },
            label = { Text(text = "0") })
    }
}

@Composable
fun SubmissionHeaderContent(
    submission: SubmissionUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.normal)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                val updatedAt = submission.updatedAt?.toString("dd MMM HH:mm")
                val title = submission.grade?.let {
                    "Оценено: ${it.value}/5"
                } ?: when (submission.state) {
                    SubmissionState.NEW,
                    SubmissionState.CREATED,
                    -> "Не сдано"

                    SubmissionState.SUBMITTED -> "Сдано"
                    SubmissionState.CANCELED_BY_AUTHOR -> "Отменено"
                }
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                updatedAt?.let {
                    Text(text = it, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun SubmissionSheetExpanded(
    uiState: SubmissionUiState,
    modifier: Modifier,
    onAttachmentClick: (AttachmentItem) -> Unit,
    onAttachmentAdd: () -> Unit,
    onAttachmentRemove: (attachmentId: UUID) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        modifier.fillMaxWidth()
    ) {
        SubmissionDetailsContent(uiState, onAttachmentClick, onAttachmentRemove)
        Spacer(modifier = Modifier.weight(1f))
        Row(Modifier.padding(MaterialTheme.spacing.normal)) {
            when (uiState.state) {
                SubmissionState.NEW,
                SubmissionState.CREATED,
                SubmissionState.CANCELED_BY_AUTHOR,
                -> {
                    OutlinedButton(
                        onClick = { onAttachmentAdd() },
                        modifier = Modifier.weight(2f),
                    ) {
                        Text(text = "Прикрепить файл")
                    }
                    if (uiState.attachments.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.normal))
                        Button(onClick = { onSubmit() }, modifier = Modifier) {
                            Text(text = "Сдать работу")
                        }
                    }
                }

                SubmissionState.SUBMITTED -> FilledTonalButton(
                    onClick = { onCancel() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(text = "Отменить отправку")
                }
            }
        }
    }
}

@Preview
@Composable
fun CourseWorkContentPreview() {
    AppTheme {
        Surface {
            CourseWorkContent(
                childrenResource = Resource.Loading, submissionResource = Resource.Success(
                    SubmissionUiState(
                        id = UUID.randomUUID(),
                        author = Author(UUID.randomUUID(),"","",""),
                        state = SubmissionState.CREATED,
                        attachments = listOf(
                            AttachmentItem.FileAttachmentItem(
                                "file",
                                null,
                                UUID.randomUUID(),
                                FileState.Preview,
                                "".toPath()
                            )
                        ),
                        updatedAt = null,
                        grade = null
                    )
                ), onAttachmentAdd = {},
                onAttachmentClick = {},
                onAttachmentRemove = {},
                onSubmit = {},
                onCancel = {},
                submissionExpanded = false,
                onSubmissionExpandChange = {}
            )
        }
    }
}


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