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
import com.denchic45.kts.WindowWidthSizeClass
import com.denchic45.kts.ui.CompactMasterDetailLayout
import com.denchic45.kts.ui.MasterDetailSidebarContent
import com.denchic45.kts.ui.ResponsiveContent
import com.denchic45.kts.ui.chooser.SearchScreen
import com.denchic45.kts.ui.chooser.UserListItem
import com.denchic45.kts.ui.model.UserItem
import com.denchic45.kts.ui.profile.ProfileScreen
import com.denchic45.kts.ui.theme.calculateWindowSizeClass
import com.denchic45.kts.ui.usereditor.UserEditorScreen

@Composable
fun UsersAdminScreen(component: UsersAdminComponent) {
    val childOverlay by component.childOverlay.subscribeAsState()
    ResponsiveContent(
        compactContent = {
            CompactMasterDetailLayout(
                masterContent = { /*TODO*/ },
                detailContent = childOverlay.overlay?.let {
                    {
                        when (val child = it.instance) {
                            is UsersAdminComponent.Child.Profile -> {
                                ProfileScreen(child.component)
                            }

                            is UsersAdminComponent.Child.UserEditor -> {
                                UserEditorScreen(child.component)
                            }
                        }
                    }
                })
        },
        mediumDrawContent = {

        },
        expandedDrawContent =
    )



    MasterDetailSidebarContent(
        masterContent = {
            UsersAdminContent(component)
        }, detailContent = {
            childOverlay.overlay?.let {
                when (val child = it.instance) {
                    is UsersAdminComponent.Child.Profile -> ProfileScreen(component = child.component)
                    is UsersAdminComponent.Child.UserEditor -> {
                        when (val width = calculateWindowSizeClass().widthSizeClass) {
                            WindowWidthSizeClass.Compact -> UserEditorScreen(component = child.component)
                        }
                    }
                }
            }

        })
}

@Composable
private fun UsersAdminContent(component: UsersAdminComponent) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = component::onAddClick) {
                Icon(Icons.Default.Add, "add course")
            }
        }) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            SearchScreen(
                component = component.chooserComponent,
                keyItem = UserItem::id
            ) { item ->
                UserListItem(item)
            }
        }
    }
}