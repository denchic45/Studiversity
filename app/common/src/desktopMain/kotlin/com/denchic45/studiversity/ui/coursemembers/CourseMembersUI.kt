package com.denchic45.studiversity.ui.coursemembers

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.ui.BlockContent
import com.denchic45.studiversity.ui.ScreenScaffold
import com.denchic45.studiversity.ui.confirm.ConfirmDeletionDialog
import com.denchic45.studiversity.ui.main.AppBarTitle
import com.denchic45.studiversity.ui.main.CustomAppBar
import com.denchic45.studiversity.ui.member.MemberListItem
import com.denchic45.studiversity.ui.membereditor.MemberRolesEditorDialog
import java.util.*

@Composable
fun CourseMembersScreen(component: CourseMembersComponent, allowEdit: Boolean) {
    val members by component.members.collectAsState()

    val childSlot by component.childSlot.subscribeAsState()
    childSlot.child?.let {
        when (val child = it.instance) {
            is CourseMembersComponent.SlotChild.EnrolMembers -> {
                EnrolCourseMembersDialog(child.component)
            }

            is CourseMembersComponent.SlotChild.MemberEditor -> {
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
        ScreenScaffold(topBar = {
            CustomAppBar(
                title = { AppBarTitle("Участники") },
                actions = {
                    if (allowEdit)
                        Button(onClick = component::onEnrolMembersClick) {
                            Icon(Icons.Outlined.Add, null)
                            Text("Добавить участника")
                        }
                })
        }) {
            CourseMembersContent(
                membersResource = members,
                memberItemContent = { member ->
                    MemberListItem(
                        item = member,
                        allowEdit = allowEdit,
                        onClick = { component.onMemberClick(member.id) },
                        onEditClick = { component.onEditMemberClick(member) },
                        onRemoveClick = { component.onRemoveMemberClick(member.id) }
                    )
                }
            )
        }
    }
}