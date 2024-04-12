package com.denchic45.studiversity.ui.coursemembers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.denchic45.studiversity.ui.member.MemberListItem

@Composable
fun CourseMembersScreen(component: CourseMembersComponent, allowEdit: Boolean) {
    val members by component.members.collectAsState()

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
        },
    )
}