package com.denchic45.studiversity.ui.search

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.common.R
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse

@Composable
fun SpecialtyChooserScreen(component: SpecialtyChooserComponent) {
    SearchScreen(
        component = component,
        keyItem = { it.id }, itemContent = {
            SpecialtyListItem(it)
        })
}

@Composable
fun SpecialtyListItem(item: SpecialtyResponse, trailingContent: (@Composable () -> Unit)? = null) {
    ListItem(
        headlineContent = { Text(item.name) },
        leadingContent = {
            Icon(
                painter = painterResource(R.drawable.ic_specialty),
                contentDescription = "Specialty icon",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
        },
        trailingContent = trailingContent,
    )
}