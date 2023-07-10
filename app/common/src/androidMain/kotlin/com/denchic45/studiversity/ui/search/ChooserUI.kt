package com.denchic45.studiversity.ui.search

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.common.R
import com.denchic45.studiversity.ui.IconTitleBox
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.appbar2.hideAppBar
import com.denchic45.studiversity.ui.theme.spacing

@Composable
fun <T> SearchScreen(
    component: SearchableComponent<T>,
    keyItem: (T) -> Any,
    emptyQueryContent: (@Composable () -> Unit)? = { StartSearch() },
    emptyResultContent: (@Composable () -> Unit)? = { EmptySearch() },
    placeholder: String = "Поиск",
    itemContent: @Composable (T) -> Unit,
) {
    hideAppBar()
    Surface {
        SearchContent(
            component = component,
            keyItem = keyItem,
            emptyQueryContent = emptyQueryContent,
            emptyResultContent = emptyResultContent,
            placeholder = placeholder,
            itemContent = itemContent
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun <T> SearchContent(
    component: SearchableComponent<T>,
    keyItem: (T) -> Any,
    emptyQueryContent: @Composable (() -> Unit)?,
    emptyResultContent: @Composable (() -> Unit)?,
    placeholder: String,
    itemContent: @Composable (T) -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        var query by remember { mutableStateOf(component.query.value) }
        SearchBar(
            query = query,
            onQueryChange = {
                component.onQueryChange(it)
                query = it
            },
            placeholder = { Text(text = placeholder) },
            onSearch = { },
            active = false,
            onActiveChange = { },
            modifier = Modifier.align(Alignment.TopCenter),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "search"
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = {
                        component.onQueryChange("")
                        query = ""
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "clear query"
                        )
                    }
                }
            }
        ) {}
        SearchedItemsContent(
            component = component,
            keyItem = keyItem,
            emptyQueryContent = emptyQueryContent,
            emptyResultContent = emptyResultContent,
            itemContent = itemContent
        )
    }
}

@Composable
fun <T> SearchedItemsContent(
    component: SearchableComponent<T>,
    keyItem: (T) -> Any,
    emptyQueryContent: @Composable (() -> Unit)?,
    emptyResultContent: (@Composable () -> Unit)?,
    itemContent: @Composable (T) -> Unit,
) {
    val searchState by component.searchState.collectAsState()

    Crossfade(targetState = searchState) {
        when (val state = it) {
            SearchState.EmptyQuery -> if (emptyQueryContent != null) {
                emptyQueryContent()
            }

            is SearchState.Result -> {
                ResourceContent(resource = state.items) { items ->
                    if (items.isNotEmpty()) {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                top = 72.dp,
                                bottom = MaterialTheme.spacing.medium
                            )
                        ) {
                            items(items, key = keyItem) {
                                Box(modifier = Modifier.clickable { component.onItemClick(it) }) {
                                    itemContent(it)
                                }
                            }
                        }
                    } else {
                        emptyResultContent?.let { emptyResultContent() }
                    }
                }
            }
        }
    }
}

@Composable
fun StartSearch() {
    IconTitleBox(icon = {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "search",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(78.dp)
        )
    }, title = {
        Text(text = "Начните искать")
    })
}

@Composable
fun EmptySearch() {
    IconTitleBox(
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ill_404),
                contentDescription = "not found",
                tint = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(78.dp)
            )
        }, title = { Text(text = "Ничего не найдено") }
    )
}