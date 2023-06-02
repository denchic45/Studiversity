package com.denchic45.kts.ui.chooser

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onLoading
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.theme.spacing

@Composable
fun <T> SearchScreen(
    component: SearchableComponent<T>,
    keyItem: (T) -> Any,
    itemContent: @Composable (T) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        SearchContent(component, keyItem, Modifier.width(500.dp), itemContent)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun <T> SearchContent(
    component: SearchableComponent<T>,
    keyItem: (T) -> Any,
    modifier: Modifier,
    itemContent: @Composable (T) -> Unit,
) {
    Box(modifier) {
        var query by remember { mutableStateOf(component.query.value) }
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                component.onQueryChange(it)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "search"
                )
            }
        )

        SearchedItemsContent(keyItem, component, itemContent)
    }
}

@Composable
fun <T> SearchedItemsContent(
    keyItem: (T) -> Any,
    component: SearchableComponent<T>,
    itemContent: @Composable (T) -> Unit,
) {
    val itemsResource: Resource<List<T>> by component.foundItems.collectAsState()
    itemsResource.onSuccess {
        LazyColumn(
            contentPadding = PaddingValues(
                top = 64.dp,
                bottom = MaterialTheme.spacing.medium
            )
        ) {
            items(it, key = keyItem) {
                Box(modifier = Modifier.clickable { component.onItemClick(it) }) {
                    itemContent(it)
                }
            }
        }
    }.onLoading {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}