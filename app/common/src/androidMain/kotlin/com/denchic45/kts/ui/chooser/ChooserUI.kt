package com.denchic45.kts.ui.chooser

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.denchic45.kts.domain.onLoading
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ChooserScreen(
    component: ChooserComponent<T>,
    keyItem: (T) -> Any,
    itemContent: @Composable (T) -> Unit
) {
    val itemsResource by component.items.collectAsState()
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth()) {
            val query by component.query.collectAsState()
            var active by remember { mutableStateOf(false) }
            SearchBar(
                query = query,
                onQueryChange = component::onQueryChange,
                onSearch = { active = false },
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
            itemsResource.onSuccess {
                LazyColumn(contentPadding = PaddingValues(vertical = MaterialTheme.spacing.medium)) {
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
    }
}
