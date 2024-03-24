package com.denchic45.studiversity.ui.coursework

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.resource.takeValueIfSuccess
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.appbar.AppBarContent
import com.denchic45.studiversity.ui.appbar.DropdownMenuItem2
import com.denchic45.studiversity.ui.appbar.updateAppBarState
import com.denchic45.studiversity.ui.component.TabIndicator
import com.denchic45.studiversity.ui.theme.AppTheme
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.studiversity.util.OpenMultipleAnyDocuments
import com.denchic45.studiversity.util.getFile
import com.denchic45.stuiversity.api.submission.model.SubmissionState
import com.denchic45.stuiversity.util.toString
import kotlinx.coroutines.launch

@Composable
fun CourseWorkScreen(component: CourseWorkComponent) {
    val childrenResource by component.children.collectAsState()
    val yourSubmissionComponent by component.yourSubmissionComponent.collectAsState()
    val allowEdit by component.allowEditWork.collectAsState()

    val context = LocalContext.current

    updateAppBarState(
        allowEdit, AppBarContent(
            dropdownItems = if (allowEdit.takeValueIfSuccess() == true)
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

    val submissionMeasurement = remember { SubmissionMeasurement() }
    var submissionExpanded by remember { mutableStateOf(false) }

    BackHandler(submissionExpanded) {
        submissionExpanded = false
    }

    CourseWorkLayout(
        childrenResource = childrenResource,
        submissionMeasurement = submissionMeasurement,
        submissionExpanded = submissionExpanded,
        submissionContent = {
            yourSubmissionComponent.onSuccess {
                it?.let { yourSubmissionComponent ->
                    val submissionResource by yourSubmissionComponent.submission.collectAsState()
                    val submissionAttachmentsComponent by yourSubmissionComponent.attachmentsComponentResource.collectAsState()
                    ResourceContent(submissionAttachmentsComponent) { attachmentComponent ->
                        val attachmentsIsEmpty by attachmentComponent.isEmpty().collectAsState()

                        val pickFileLauncher = rememberLauncherForActivityResult(
                            OpenMultipleAnyDocuments()
                        ) { uris ->
                            yourSubmissionComponent.onFilesSelect(uris.map { it.getFile(context) })
                        }

                        val transition =
                            calcPercentOf(
                                0.2F,
                                0.8F,
                                maxOf(0.2F, minOf(submissionMeasurement.difference, 0.8F))
                            )

                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            submissionResource.onSuccess { submission ->
                                SubmissionHeaderContent(
                                    submission,
                                    Modifier
                                        .onGloballyPositioned { coordinates ->
                                            submissionMeasurement.headerHeight =
                                                coordinates.size.height.toFloat()
                                        }
                                        .padding(top = MaterialTheme.spacing.normal)
                                )
                                Box(Modifier.fillMaxHeight()) {
                                    Column(Modifier.fillMaxHeight()) {
                                        SubmissionSheetExpanded(
                                            uiState = submission,
                                            attachmentsIsEmpty.takeValueIfSuccess() ?: true,
                                            modifier = Modifier
                                                .height(submissionMeasurement.screenHeight.pxToDp() + submissionMeasurement.collapsedHeight.pxToDp())
                                                .alpha(transition)
                                                .clickable(enabled = false, onClick = {}),
                                            attachmentContent = {
                                                ResourceContent(submissionAttachmentsComponent) {
                                                    SubmissionAttachments(it)
                                                }
                                            },
                                            onAttachmentAdd = { pickFileLauncher.launch(Unit) },
                                            onSubmit = yourSubmissionComponent::onSubmit,
                                            onCancel = yourSubmissionComponent::onCancel
                                        )
                                    }

                                    if (transition < 1F)
                                        SubmissionCollapsedContent(
                                            submission = submission,
                                            attachmentIsEmpty = attachmentsIsEmpty.takeValueIfSuccess()
                                                ?: true,
                                            modifier = Modifier
                                                .clickable(enabled = false, onClick = {})
                                                .alpha(1 - transition)
                                                .onGloballyPositioned { coordinates ->
                                                    submissionMeasurement.collapsedHeight =
                                                        coordinates.size.height.toFloat()
                                                },
                                            onAttachmentAdd = { pickFileLauncher.launch(Unit) },
                                            onSubmit = yourSubmissionComponent::onSubmit,
                                            onCancel = yourSubmissionComponent::onCancel
                                        )
                                }
                            }
                        }
                    }
                }
            }
        },
        onSubmissionExpandChange = { submissionExpanded = it }
    )
}

@Stable
class SubmissionMeasurement {
    var screenHeight by mutableFloatStateOf(0F)
    var headerHeight by mutableFloatStateOf(0F)
    var collapsedHeight by mutableFloatStateOf(0f)
    var offset by mutableFloatStateOf(0F)

    val difference = if (screenHeight != 0F) {
        (screenHeight - offset) / screenHeight
    } else 0F
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CourseWorkLayout(
    childrenResource: Resource<List<CourseWorkComponent.Child>>,
    submissionMeasurement: SubmissionMeasurement,
    submissionContent: @Composable () -> Unit,
    submissionExpanded: Boolean,
    onSubmissionExpandChange: (Boolean) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()


    var hidden by remember { mutableStateOf(false) }
    val bottomSheetState = rememberStandardBottomSheetState(
        skipHiddenState = false,
        confirmValueChange = { it != SheetValue.Hidden }
    )

    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)
    val topHeight = submissionMeasurement.headerHeight + submissionMeasurement.collapsedHeight
    var screenHeight by remember { mutableFloatStateOf(0F) }

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
                    submissionMeasurement.offset = it
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
        sheetSwipeEnabled = submissionMeasurement.offset != 0F,
        sheetContent = {
            submissionContent()
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
            CourseWorkContent(childrenResource) {
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
private fun CourseWorkContent(
    childrenResource: Resource<List<CourseWorkComponent.Child>>,
    onPageSelect: (Int) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    ResourceContent(resource = childrenResource) { children ->
        val pagerState = rememberPagerState(pageCount = children::size)
        LaunchedEffect(pagerState) {
            // Collect from the pager state a snapshotFlow reading the currentPage
            snapshotFlow { pagerState.currentPage }.collect { page ->
                onPageSelect(page)
            }
        }
        Column {
            if (children.size != 1) {
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    indicator = { positions ->
                        TabIndicator(Modifier.tabIndicatorOffset(positions[pagerState.currentPage]))
                    }
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
            HorizontalPager(state = pagerState) {
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
    attachmentIsEmpty: Boolean,
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
                        imageVector = Icons.AutoMirrored.Outlined.Comment,
                        contentDescription = "comments"
                    )
                },
                label = { Text(text = "0") })
        }
        if (submission.grade == null)
            when (submission.state) {
                SubmissionState.CREATED,
                SubmissionState.CANCELED_BY_AUTHOR,
                -> {
                    Button(
                        onClick = {
                            if (attachmentIsEmpty) onAttachmentAdd()
                            else onSubmit()
                        }, modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = if (attachmentIsEmpty) "Добавить" else "Сдать")
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
//                    SubmissionState.SUBMITTED -> Button(onClick = onCancel) {
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
                Text(text = submission.title, style = MaterialTheme.typography.titleLarge)
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
    attachmentIsEmpty: Boolean,
    attachmentContent: @Composable () -> Unit,
    modifier: Modifier,
    onAttachmentAdd: () -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        modifier.fillMaxWidth()
    ) {
        attachmentContent()
        Spacer(modifier = Modifier.weight(1f))
        Column(Modifier.padding(MaterialTheme.spacing.normal)) {
            when (uiState.state) {
                SubmissionState.CREATED,
                SubmissionState.CANCELED_BY_AUTHOR,
                -> {
                    OutlinedButton(
                        onClick = { onAttachmentAdd() },
                        modifier = Modifier.weight(2f),
                    ) {
                        Text(text = "Прикрепить файл")
                    }
                    if (!attachmentIsEmpty) {
                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.normal))
                        Button(onClick = onSubmit, modifier = Modifier) {
                            Text(text = "Сдать")
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
            CourseWorkLayout(
                childrenResource = Resource.Loading,
                submissionContent = {},
                submissionMeasurement = SubmissionMeasurement(),
//                submissionResource = Resource.Success(
//                    SubmissionUiState(
//                        id = UUID.randomUUID(),
//                        author = Author(UUID.randomUUID(), "", "", ""),
//                        state = SubmissionState.CREATED,
//                        attachments = listOf(
//                            AttachmentItem.FileAttachmentItem(
//                                "file",
//                                null,
//                                UUID.randomUUID(),
//                                FileState.Preview,
//                                "".toPath()
//                            )
//                        ),
//                        updatedAt = null,
//                        grade = null
//                    )
//                ),
                submissionExpanded = false,
                onSubmissionExpandChange = {}
            )
        }
    }
}

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