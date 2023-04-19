package com.denchic45.kts.ui.courseWork

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.ui.theme.AppTheme
import kotlin.math.roundToInt

@Composable
fun CourseWorkScreen(component: CourseWorkComponent) {
    val childrenResource by component.children.collectAsState()

    CourseWorkContent(childrenResource)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CourseWorkContent(
    childrenResource: Resource<List<CourseWorkComponent.Child>>,
) {
    var collapsedHeight by remember { mutableStateOf(0) }
    var expandedHeight by remember { mutableStateOf(0) }
    var offset by remember { mutableStateOf(0) }
    val bottomSheetState = rememberStandardBottomSheetState()

    LaunchedEffect(Unit) {
        snapshotFlow { bottomSheetState.requireOffset() }
            .collect {
                offset = it.roundToInt()
            }
    }


    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)

    var screenHeight by remember {
        mutableStateOf(0)
    }


    Column {
        Text("offset ${offset.pxToDp()}")
        Text("screen height ${screenHeight.pxToDp()}")

        Text("collapsed height ${collapsedHeight.pxToDp()}")
        Text("expanded height ${expandedHeight.pxToDp()}")

        Text(
            "difference ${
                if (screenHeight != 0) {
                    ((screenHeight.toFloat()) - (offset)) / (expandedHeight.toFloat() - collapsedHeight.toFloat()) * 100.toFloat()
                } else -1
            }"
        )

//        val fraction =
//        Text("fraction $fraction ")
    }

    BottomSheetScaffold(
        sheetPeekHeight = collapsedHeight.pxToDp(),
        scaffoldState = scaffoldState,
        sheetContent = {
            Box {
                SubmissionSheetExpanded(
                    Modifier
                        .onGloballyPositioned { coordinates ->
                            // Set column height using the LayoutCoordinates
                            expandedHeight = coordinates.size.height
                        })
                SubmissionSheetCollapsed(Modifier
                    .onGloballyPositioned { coordinates ->
                        // Set column height using the LayoutCoordinates
                        collapsedHeight = coordinates.size.height
                    })
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
//            CourseWorkBody(childrenResource)
        }
    }
}

//@Composable
//@OptIn(ExperimentalFoundationApi::class)
//private fun CourseWorkBody(
//    childrenResource: Resource<List<CourseWorkComponent.Child>>,
//) {
//    val coroutineScope = rememberCoroutineScope()
//    val pagerState = rememberPagerState()
//    childrenResource.onSuccess { children ->
//        Column {
//            TabRow(
//                selectedTabIndex = pagerState.currentPage
//            ) {
//                // Add tabs for all of our pages
//                children.forEachIndexed { index, child ->
//                    Tab(
//                        text = { Text(child.title) },
//                        selected = pagerState.currentPage == index,
//                        onClick = {
//                            coroutineScope.launch {
//                                pagerState.animateScrollToPage(index)
//                            }
//                        },
//                    )
//                }
//            }
//
//            HorizontalPager(state = pagerState, pageCount = children.size) {
//                Box(modifier = Modifier.fillMaxSize()) {
//                    when (val child = children[it]) {
//                        is CourseWorkComponent.Child.Details -> CourseWorkDetailsScreen(child.component)
//                        is CourseWorkComponent.Child.Submissions -> CourseWorkSubmissionsScreen(
//                            child.component
//                        )
//                    }
//                }
//            }
//        }
//    }
//}

@Composable
fun SubmissionSheetCollapsed(modifier: Modifier) {
    Column(
        modifier
    ) {
        repeat(5) {
            Text(text = "Expanded $it", color = Color.Blue)
        }
    }
}

@Composable
fun SubmissionSheetExpanded(modifier: Modifier) {
    Column(
        modifier
    ) {
        repeat(15) {
            Text(text = "Expanded $it", color = Color.Red)
        }
    }
}

@Preview
@Composable
fun CourseWorkContentPreview() {
    AppTheme {
        Surface {
            CourseWorkContent(childrenResource = Resource.Loading)
        }
    }
}


@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }


@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }