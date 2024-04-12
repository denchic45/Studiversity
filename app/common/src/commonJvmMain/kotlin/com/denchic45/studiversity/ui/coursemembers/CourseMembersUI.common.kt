package com.denchic45.studiversity.ui.coursemembers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.denchic45.studiversity.domain.model.UserItem
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.component.HeaderItem
import com.denchic45.studiversity.ui.theme.spacing
import java.util.*


@Composable
fun CourseMembersContent(
    membersResource: Resource<CourseMembersState>,
    memberItemContent: @Composable (UserItem) -> Unit
) {
    ResourceContent(membersResource) { members ->
        LazyColumn(contentPadding = PaddingValues(vertical = MaterialTheme.spacing.normal)) {
            members.teachers.takeIf(List<UserItem>::isNotEmpty)?.let { teachers ->
                item { HeaderItem(if (teachers.size == 1) "Преподаватель" else "Преподаватели") }
                items(teachers, key = { it.id }) { member -> memberItemContent(member) }
            }

            members.students.takeIf(List<UserItem>::isNotEmpty)?.let { students ->
                item { HeaderItem("Студенты") }
                items(students, key = { it.id }) { member -> memberItemContent(member) }
            }

            members.otherMembers.takeIf(List<UserItem>::isNotEmpty)?.let { otherMembers ->
                item { HeaderItem("Другие участники") }
                items(otherMembers, key = { it.id }) { member -> memberItemContent(member) }
            }
        }
    }
}