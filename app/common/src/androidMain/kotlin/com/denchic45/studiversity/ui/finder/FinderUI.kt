package com.denchic45.studiversity.ui.finder

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Divider
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.isContainer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.zIndex
import com.denchic45.studiversity.ui.component.TabIndicator
import com.denchic45.studiversity.ui.model.UserItem
import com.denchic45.studiversity.ui.search.SearchedItemsContent
import com.denchic45.studiversity.ui.search.UserListItem
import com.denchic45.studiversity.ui.studygroups.StudyGroupListItem
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import kotlinx.coroutines.launch

@Composable
fun FinderScreen(component: FinderComponent) {
    val children = component.children
    val selectedTab by component.selectedTab.collectAsState()
    val query by component.query.collectAsState()

    FinderContent(
        children = children,
        query = query,
        selectedTab = selectedTab,
        onQueryChange = component::onQueryChange,
        onTabSelect = component::onTabSelect
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FinderContent(
    children: List<FinderComponent.TabChild>,
    query: String,
    selectedTab: Int,
    onQueryChange: (String) -> Unit,
    onTabSelect: (Int) -> Unit
) {
    val activeChild = children[selectedTab]

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = children::size)

    LaunchedEffect(pagerState) {
        snapshotFlow(pagerState::currentPage).collect(onTabSelect)
    }

    Box(modifier = Modifier
        .semantics { isContainer = true }
        .zIndex(1f)
        .fillMaxWidth()) {
        SearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = { },
            active = false,
            onActiveChange = { },
            modifier = Modifier.align(Alignment.TopCenter),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "search"
                )
            }
        ) {}
    }
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.primary,
        indicator = { tabPositions -> TabIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedTab])) },
        divider = {}) {
        children.indices.forEach { index ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                text = {
                    Text(
                        when (activeChild) {
                            is FinderComponent.TabChild.Users -> "Пользователи"
                            is FinderComponent.TabChild.StudyGroups -> "Группы"
                        }
                    )
                }
            )
        }
    }
    Divider()
    HorizontalPager(
        state = pagerState
    ) {
        Box(modifier = Modifier.fillMaxHeight()) {
            when (activeChild) {
                is FinderComponent.TabChild.Users -> SearchedItemsContent(
                    component = activeChild.component,
                    keyItem = UserItem::id,
                    null,
                    null,
                ) { UserListItem(it) }

                is FinderComponent.TabChild.StudyGroups -> SearchedItemsContent(
                    component = activeChild.component,
                    keyItem = StudyGroupResponse::id,
                    null,
                    null,
                ) { StudyGroupListItem(it) }
            }
        }
    }
}
