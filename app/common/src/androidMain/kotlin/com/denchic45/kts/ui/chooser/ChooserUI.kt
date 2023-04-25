package com.denchic45.kts.ui.chooser

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.denchic45.kts.domain.onLoading
import com.denchic45.kts.domain.onSuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ChooserScreen(
    component: ChooserComponent<T>,
    keyItem: (T) -> Any,
    itemContent: @Composable (T) -> Unit
) {
    val itemsResource by component.items.collectAsState()
    itemsResource.onSuccess {
        Column(Modifier.fillMaxWidth()) {
            val query by component.query.collectAsState()
            SearchBar(
                query = query,
                onQueryChange = component::onQueryChange,
                onSearch = {},
                active = true,
                onActiveChange = {},
            ) {
                LazyColumn {
                    items(it, key = keyItem) {
                        Box(modifier = Modifier.clickable { component.onItemClick(it) }) {
                            itemContent(it)
                        }
                    }
                }
            }
        }
    }.onLoading {
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
    }
}
