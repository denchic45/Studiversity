package com.denchic45.kts.ui.courseWork

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.courseWork.details.CourseWorkDetailsScreen
import com.denchic45.kts.ui.courseWork.submissions.CourseWorkSubmissionsScreen
import com.denchic45.kts.ui.courseWork.yourSubmission.YourSubmissionComponent
import com.denchic45.kts.ui.theme.AppTheme
import com.denchic45.kts.ui.theme.spacing
import com.denchic45.stuiversity.api.course.work.submission.model.Author
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionResponse
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionState
import com.denchic45.stuiversity.api.course.work.submission.model.WorkSubmissionContent
import com.denchic45.stuiversity.api.course.work.submission.model.WorkSubmissionResponse
import com.denchic45.stuiversity.util.toString
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import okio.Path.Companion.toOkioPath
import java.util.UUID
import kotlin.math.roundToInt

@Composable
fun CourseWorkScreen(component: CourseWorkComponent) {
    val childrenResource by component.children.collectAsState()
    val childOverlay by component.childOverlay.subscribeAsState()

    val yourSubmissionComponent = childOverlay.overlay?.instance?.component
    val submissionResource by (yourSubmissionComponent?.uiState ?: emptyFlow())
        .collectAsState(null)

    val pickFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data: Intent ->
                yourSubmissionComponent!!.onAttachmentSelect(data.data!!.toFile().toOkioPath())
            }
        }
    }

    CourseWorkContent(childrenResource, submissionResource, onAttachmentAdd = {
        val chooserIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }

        pickFileLauncher.launch(
            Intent.createChooser(
                chooserIntent, "Выберите файл"
            )
        )
    },component::onSubmit,component::onCancel)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CourseWorkContent(
    childrenResource: Resource<List<CourseWorkComponent.Child>>,
    submissionResource: Resource<YourSubmissionComponent.SubmissionUiState>?,
    onAttachmentAdd: () -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit
) {
    var collapsedHeight by remember { mutableStateOf(0) }
    var expandedHeight by remember { mutableStateOf(0) }
    var offset by remember { mutableStateOf(0) }
    val bottomSheetState = rememberStandardBottomSheetState()

    LaunchedEffect(Unit) {
        snapshotFlow(bottomSheetState::requireOffset)
            .collect {
                offset = it.roundToInt()
            }
    }


    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)

    var screenHeight by remember {
        mutableStateOf(0)
    }

    val difference = if (screenHeight != 0) {
        val expandedOrScreen = minOf(screenHeight, expandedHeight)
        ((expandedOrScreen.toFloat()) - (offset)) / (expandedOrScreen.toFloat()
                - collapsedHeight.toFloat()) * 1F
    } else 0F

    val transition = calcPercentOf(0.2F, 0.8F, maxOf(0.2F, minOf(difference, 0.8F)))

    Column {
        Text("offset ${offset.pxToDp()}")
        Text("screen height ${screenHeight.pxToDp()}")

        Text("collapsed height ${collapsedHeight.pxToDp()}")
        Text("expanded height ${expandedHeight.pxToDp()}")
    }

    BottomSheetScaffold(
        sheetPeekHeight = collapsedHeight.pxToDp(),
        scaffoldState = scaffoldState,
        sheetSwipeEnabled = offset != 0,
        sheetContent = {
            Box(
                Modifier
            ) {
                submissionResource?.onSuccess { submission ->
                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                        Column {
                            Text("difference $difference")
                            Text("transition: $transition")
                        }
                    }
                    SubmissionSheetExpanded(
                        Modifier
                            .verticalScroll(rememberScrollState())
                            .alpha(transition)
                            .clickable(enabled = false, onClick = {})
                            .onGloballyPositioned { coordinates ->
                                expandedHeight = coordinates.size.height
                            })
                    if (transition < 1F)
                        SubmissionSheetCollapsed(
                            submission,
                            Modifier
                                .clickable(enabled = false, onClick = {})
                                .alpha(1 - transition)
                                .onGloballyPositioned { coordinates ->
                                    collapsedHeight = coordinates.size.height
                                },
                            onAttachmentAdd,
                            onSubmit,
                            onCancel
                        )
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
                    screenHeight = coordinates.size.height
                }) {
            CourseWorkBody(childrenResource)
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
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    childrenResource.onSuccess { children ->
        Column {
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
fun SubmissionSheetCollapsed(
    submission: YourSubmissionComponent.SubmissionUiState,
    modifier: Modifier,
    onAttachmentAdd: () -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.medium)
    ) {
        Row {
            Column(Modifier.weight(1f)) {
                val updatedAt = submission.updatedAt?.toString("dd MMM HH:mm")
                val title = submission.grade?.let {
                    "Оценено: ${it.value}/5"
                } ?: when (submission.state) {
                    SubmissionState.NEW,
                    SubmissionState.CREATED -> "Не сдано"

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
                    SubmissionState.CANCELED_BY_AUTHOR -> {
                        Button(onClick = {
                            if (submission.attachments.isEmpty()) onAttachmentAdd()
                            else onCancel()
                        }) {
                            Text(text = if (submission.attachments.isEmpty()) "Добавить" else "Сдать")
                        }
                    }

                    SubmissionState.SUBMITTED -> Button(onClick = { /*TODO*/ }) {
                        Text(text = "Отменить")
                    }

                }
        }
    }
}

@Composable
fun SubmissionSheetExpanded(modifier: Modifier) {
    val current = LocalContext.current
    Column(
        modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.medium)
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Button(onClick = {
            Toast.makeText(current, "Expanded click", Toast.LENGTH_SHORT).show()
        }) {
            Text(text = "Expanded")
        }
        repeat(55) {
            Text(text = "Expanded $it", color = Color.Red)
        }
    }
}

@Preview
@Composable
fun CourseWorkContentPreview() {
    AppTheme {
        Surface {
            CourseWorkContent(
                childrenResource = Resource.Loading,
                submissionResource = Resource.Success(
                    YourSubmissionComponent.SubmissionUiState(
                        state = SubmissionState.CREATED,
                        attachments = emptyList(),
                        updatedAt = null,
                        grade = null
                    )
                ),
                {}, {}, {}
            )
        }
    }
}


@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }


@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

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