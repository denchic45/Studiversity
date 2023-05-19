package com.denchic45.kts.ui.finder

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Divider
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.denchic45.kts.ui.chooser.SearchedItemsContent
import com.denchic45.kts.ui.chooser.StudyGroupListItem
import com.denchic45.kts.ui.chooser.UserListItem
import com.denchic45.kts.ui.component.TabIndicator
import com.denchic45.kts.ui.model.UserItem
import com.denchic45.kts.ui.theme.spacing
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
    val pagerState = rememberPagerState()

    LaunchedEffect(pagerState) {
        snapshotFlow(pagerState::currentPage).collect(onTabSelect)
    }

    Column {
        SearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = { },
            active = false,
            onActiveChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.spacing.normal),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "search"
                )
            }
        ) {}
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
            state = pagerState,
            pageCount = children.size,
        ) {
            Box(modifier = Modifier.fillMaxHeight()) {
                when (activeChild) {
                    is FinderComponent.TabChild.Users -> SearchedItemsContent(
                        keyItem = UserItem::id,
                        component = activeChild.component,
                        itemContent = { UserListItem(it) }
                    )

                    is FinderComponent.TabChild.StudyGroups -> SearchedItemsContent(
                        keyItem = StudyGroupResponse::id,
                        component = activeChild.component,
                        itemContent = { StudyGroupListItem(it) }
                    )
                }
            }
        }
    }
}
