package com.denchic45.kts.ui.chooser

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse

@Composable
fun StudyGroupChooserScreen(
    component: StudyGroupChooserComponent,
    appBarInteractor: AppBarInteractor
) {
    ChooserScreen(
        component = component,
        appBarInteractor = appBarInteractor,
        keyItem = { it.id },
        itemContent = {
            StudyGroupItemUI(it)
        })
}

@Composable
fun StudyGroupItemUI(response: StudyGroupResponse, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = { Text(response.name) },
        leadingContent = {
            Icon(imageVector = Icons.Outlined.Group, contentDescription = "group icon")
        },
        modifier = modifier
    )
}