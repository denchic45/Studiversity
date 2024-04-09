package com.denchic45.studiversity.ui.studygroupmembers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.domain.model.UserItem
import com.denchic45.studiversity.domain.resource.takeValueIfSuccess
import com.denchic45.studiversity.ui.BlockContent
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.ScreenScaffold
import com.denchic45.studiversity.ui.component.HeaderItem
import com.denchic45.studiversity.ui.confirm.ConfirmDeletionDialog
import com.denchic45.studiversity.ui.main.AppBarTitle
import com.denchic45.studiversity.ui.main.CustomAppBar
import com.denchic45.studiversity.ui.member.MemberListItem
import com.denchic45.studiversity.ui.membereditor.MemberRolesEditorDialog
import com.denchic45.studiversity.ui.theme.spacing
import java.util.*


@Composable
fun StudyGroupMembersScreen(component: StudyGroupMembersComponent) {
    val allowEditMembers by component.allowEditMembers.collectAsState()
    val members by component.members.collectAsState()

    val childSlot by component.childSlot.subscribeAsState()
    childSlot.child?.let {
        when (val child = it.instance) {
            is StudyGroupMembersComponent.SlotChild.InviteMembers -> {
                InviteStudyGroupMembersDialog(child.component)
            }

            is StudyGroupMembersComponent.SlotChild.MemberEditor -> {
                MemberRolesEditorDialog(child.component)
            }
        }
    }

    var confirmDeletion by remember { mutableStateOf<UUID?>(null) }
    confirmDeletion?.let { userId ->
        ConfirmDeletionDialog(
            title = "Удалить участника группы?",
            text = "Данные, связанные с участников в группе, невозможно будет восстановить.",
            onConfirm = {
                component.onRemoveMemberClick(userId)
                confirmDeletion = null
            },
            onDismiss = {
                confirmDeletion = null
            }
        )
    }

    BlockContent {
        ResourceContent(members) {
            ScreenScaffold(topBar = {
                CustomAppBar(
                    title = { AppBarTitle("Участники") },
                    actions = {
                        if (allowEditMembers.takeValueIfSuccess() == true)
                            Button(onClick = component::onAddMemberClick) {
                                Icon(Icons.Outlined.Add, null)
                                Text("Добавить участника")
                            }
                    })
            }) {
                StudyGroupMembersContent(
                    state = it,
                    onClick = component::onMemberClick,
                    allowEditMembers = allowEditMembers.takeValueIfSuccess() == true,
                    onMemberEditClick = component::onEditMemberClick,
                    onMemberRemoveClick = { confirmDeletion = it }
                )
            }
        }
    }
}

@Composable
fun StudyGroupMembersContent(
    state: StudyGroupMembersState,
    onClick: (UUID) -> Unit,
    allowEditMembers: Boolean,
    onMemberEditClick: (UserItem) -> Unit,
    onMemberRemoveClick: (UUID) -> Unit,
) {
    val memberItem: @Composable (UserItem) -> Unit = {
        MemberListItem(
            item = it,
            allowEdit = allowEditMembers,
            onClick = { onClick(it.id) },
            onEditClick = { onMemberEditClick(it) },
            onRemoveClick = { onMemberRemoveClick(it.id) }
        )
    }
    LazyColumn(contentPadding = PaddingValues(bottom = MaterialTheme.spacing.normal)) {
        state.curators.takeIf(List<UserItem>::isNotEmpty)?.let { curators ->
            item { HeaderItem(if (curators.size == 1) "Куратор" else "Кураторы") }
            items(curators, key = { it.id }) { memberItem(it) }
        }

        state.students.takeIf(List<UserItem>::isNotEmpty)?.let { students ->
            item { HeaderItem("Студенты") }
            items(students, key = { it.id }) { memberItem(it) }
        }

        state.otherMembers.takeIf(List<UserItem>::isNotEmpty)?.let { otherMembers ->
            item { HeaderItem("Другие участники") }
            items(otherMembers, key = { it.id }) { memberItem(it) }
        }
    }
}