package com.denchic45.kts.ui.admindashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.kts.ui.search.SearchScreen
import com.denchic45.kts.ui.search.SpecialtyListItem
import com.denchic45.kts.ui.specialtyeditor.SpecialtyEditorDialog
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse

@Composable
fun SpecialtiesAdminScreen(component: SpecialtiesAdminComponent) {
    val childOverlay by component.childOverlay.subscribeAsState()

    childOverlay.overlay?.let {
        SpecialtiesAdminDetailScreen(it.instance)
    } ?: SpecialtiesAdminMainScreen(component)
}

@Composable
private fun SpecialtiesAdminDetailScreen(child: SpecialtiesAdminComponent.Child) {
    when (child) {
        is SpecialtiesAdminComponent.Child.SpecialtyEditor -> {
            SpecialtyEditorDialog(child.component)
        }
    }
}

@Composable
private fun SpecialtiesAdminMainScreen(component: SpecialtiesAdminComponent) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = component::onAddClick) {
                Icon(Icons.Default.Add, "add subject")
            }
        }) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            SearchScreen(
                component = component.chooserComponent,
                keyItem = SpecialtyResponse::id
            ) { item -> SpecialtyListItem(item) }
        }
    }
}