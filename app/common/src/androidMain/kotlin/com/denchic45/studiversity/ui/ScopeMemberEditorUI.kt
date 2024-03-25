package com.denchic45.studiversity.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.studiversity.domain.resource.resourceOf
import com.denchic45.studiversity.scopedRoleName
import com.denchic45.studiversity.ui.appbar.ActionMenuItem2
import com.denchic45.studiversity.ui.appbar.AppBarContent
import com.denchic45.studiversity.ui.appbar.updateAppBarState
import com.denchic45.studiversity.ui.component.HeaderItem
import com.denchic45.studiversity.ui.scopemembereditor.ScopeMemberEditorComponent
import com.denchic45.studiversity.ui.search.UserChooserScreen
import com.denchic45.studiversity.ui.search.UserListItem
import com.denchic45.stuiversity.api.role.model.Role


@Composable
fun ScopeMemberEditorScreen(component: ScopeMemberEditorComponent) {
    val stateResource by component.stateResource.collectAsState(resourceOf())
    val allowSave by component.allowSave.collectAsState()
    val isNew = component.isNew

    val childSlot by component.childSlot.subscribeAsState()

    updateAppBarState(
        allowSave,
        content = AppBarContent(
            title = uiTextOf(if (isNew) "Новый участник" else "Изменить участника"),
            actionItems =
            listOf(
                ActionMenuItem2(
                    icon = uiIconOf(Icons.Default.Done),
                    enabled = allowSave,
                    onClick = component::onSaveClick
                )
            )
        )
    )


    Surface(Modifier.fillMaxSize()) {
        ResourceContent(resource = stateResource) {
            ScopeMemberEditorContent(
                state = it,
                onAddUser = component::onAddUserClick,
                onRoleClick = component::onRoleClick,
            )
        }
    }

    childSlot.child?.let {
        when (val child = it.instance) {
            is ScopeMemberEditorComponent.Child.UserChooser -> {
                UserChooserScreen(child.component)
            }
        }
    }
}


@Composable
fun ScopeMemberEditorContent(
    state: ScopeMemberEditorComponent.EditableMemberState,
    onAddUser: () -> Unit,
    onRoleClick: (Role) -> Unit,
) {
    Column {
        state.user?.let {
            UserListItem(item = it)
        } ?: ListItem(
            headlineContent = { Text("Выбрать пользователя") },
            leadingContent = { Icon(Icons.Default.Add, contentDescription = "add user") },
            modifier = Modifier.clickable { onAddUser() }
        )

//        Spacer(modifier = Modifier.height(MaterialTheme.spacing.normal))
        HeaderItem("Назначить роли")

        LazyColumn {
            items(state.availableRoles) { role ->
                ListItem(
                    headlineContent = { Text(text = role.scopedRoleName()) },
                    trailingContent = {
                        if (role in state.assignedRoles) {
                            Icon(Icons.Outlined.Done, contentDescription = "assigned")
                        }
                    },
                    modifier = Modifier.clickable { onRoleClick(role) }
                )
            }
        }
    }
}