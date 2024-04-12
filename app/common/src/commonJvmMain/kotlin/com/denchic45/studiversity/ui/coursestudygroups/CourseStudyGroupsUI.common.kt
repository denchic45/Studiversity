package com.denchic45.studiversity.ui.coursestudygroups

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import com.denchic45.studiversity.domain.model.StudyGroupItem
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.studygroup.StudyGroupListItem
import java.util.*

@Composable
fun CourseStudyGroupsContent(
    studyGroupsResource: Resource<List<StudyGroupItem>>,
    onRemove: (UUID) -> Unit
) {
    ResourceContent(resource = studyGroupsResource) { studyGroups ->
        LazyColumn {
            items(studyGroups) {
                StudyGroupListItem(item = it, trailingContent = {
                    IconButton(onClick = { onRemove(it.id) }) {
                        Icon(Icons.Outlined.Delete, "delete study group")
                    }
                })
            }
        }
    }
}