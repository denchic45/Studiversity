package com.denchic45.kts.ui.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.kts.ui.admindashboard.SearchedItemsContent

@Composable
fun <T> SearchScreen(
    component: SearchableComponent<T>,
    keyItem: (T) -> Any,
    emptyQueryContent: @Composable (() -> Unit)?,
    emptyResultContent: @Composable (() -> Unit)?,
    placeholder: String,
    itemContent: @Composable (T) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        SearchContent(
            component = component,
            keyItem = keyItem,
            modifier = Modifier.width(500.dp),
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
    modifier: Modifier,
    emptyQueryContent: @Composable (() -> Unit)?,
    emptyResultContent: @Composable (() -> Unit)?,
    placeholder: String,
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
            placeholder = { Text(placeholder) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "search"
                )
            }
        )

        SearchedItemsContent(component, keyItem, emptyQueryContent, emptyResultContent, itemContent)
    }
}

//@Composable
//fun <T> SearchedItemsContent(
//    keyItem: (T) -> Any,
//    component: SearchableComponent<T>,
//    itemContent: @Composable (T) -> Unit,
//) {
//    val itemsResource: Resource<List<T>> by component.foundItems.collectAsState()
//    itemsResource.onSuccess {
//        LazyColumn(
//            contentPadding = PaddingValues(
//                top = 64.dp,
//                bottom = MaterialTheme.spacing.medium
//            )
//        ) {
//            items(it, key = keyItem) {
//                Box(modifier = Modifier.clickable { component.onItemClick(it) }) {
//                    itemContent(it)
//                }
//            }
//        }
//    }.onLoading {
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            CircularProgressIndicator()
//        }
//    }
//}