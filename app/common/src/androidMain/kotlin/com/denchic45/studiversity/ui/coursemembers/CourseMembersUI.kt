package com.denchic45.studiversity.ui.coursemembers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.denchic45.studiversity.domain.model.UserItem
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.takeValueIfSuccess
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.component.HeaderItem
import com.denchic45.studiversity.ui.member.MemberListItem
import com.denchic45.studiversity.ui.theme.spacing
import java.util.*

@Composable
fun CourseMembersScreen(component: CourseMembersComponent) {
    val members by component.members.collectAsState()
    val allowEditMembers by component.allowEdit.collectAsState()

    CourseMembersContent(
        membersResource = members,
        allowEditMembers = allowEditMembers.takeValueIfSuccess() ?: false,
        onMemberClick = component::onMemberClick,
        onMemberEditClick = component::onMemberEditClick,
        onMemberRemoveClick = component::onMemberRemoveClick
    )
}

@Composable
fun CourseMembersContent(
    membersResource: Resource<CourseMembersState>,
    allowEditMembers: Boolean,
    onMemberClick: (UUID) -> Unit,
    onMemberEditClick: (UserItem) -> Unit,
    onMemberRemoveClick: (UUID) -> Unit
) {
    ResourceContent(membersResource) { members ->
        val memberItem: @Composable (UserItem) -> Unit = { member ->
            MemberListItem(
                item = member,
                allowEdit = allowEditMembers,
                onClick = { onMemberClick(member.id) },
                onEditClick = { onMemberEditClick(member) },
                onRemoveClick = { onMemberRemoveClick(member.id) }
            )
        }
        LazyColumn(contentPadding = PaddingValues(vertical = MaterialTheme.spacing.normal)) {
            members.teachers.takeIf(List<UserItem>::isNotEmpty)?.let { teachers ->
                item { HeaderItem(if (teachers.size == 1) "Преподаватель" else "Преподаватели") }
                items(teachers, key = { it.id }) { member ->
                    memberItem(member)
                }
            }

            members.students.takeIf(List<UserItem>::isNotEmpty)?.let { students ->
                item { HeaderItem("Студенты") }
                items(students, key = { it.id }) { member ->
                    memberItem(member)
                }
            }

            members.otherMembers.takeIf(List<UserItem>::isNotEmpty)?.let { otherMembers ->
                item { HeaderItem("Другие участники") }
                items(otherMembers, key = { it.id }) { member ->
                    memberItem(member)
                }
            }
        }
    }
}