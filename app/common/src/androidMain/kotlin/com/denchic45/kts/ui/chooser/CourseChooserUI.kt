package com.denchic45.kts.ui.chooser

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.denchic45.kts.ui.appbar.AppBarInteractor

@Composable
fun CourseChooserScreen(component: CourseChooserComponent, appBarInteractor: AppBarInteractor) {
    ChooserScreen(
        component = component,
        appBarInteractor = appBarInteractor,
        keyItem = { it.id }, itemContent = {
            ListItem(
                headlineContent = { Text(it.name) },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.School,
                        contentDescription = "user avatar"
                    )
                }
            )
        })
}