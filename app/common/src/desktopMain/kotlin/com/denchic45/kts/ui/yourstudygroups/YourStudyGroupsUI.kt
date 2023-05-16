package com.denchic45.kts.ui.yourstudygroups

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.kts.domain.onLoading
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.LocalAppBarMediator
import com.denchic45.kts.ui.studygroup.StudyGroupScreen


@Composable
fun YourStudyGroupsScreen(component: YourStudyGroupsComponent) {
    val selectedStudyGroup by component.selectedStudyGroup.collectAsState()

    val appBarMediator = LocalAppBarMediator.current

    selectedStudyGroup.onSuccess {
        appBarMediator.title = it.name
    }.onLoading {
        appBarMediator.title = ""
    }

    val childStudyGroup by component.childStudyGroup.subscribeAsState()
    childStudyGroup.overlay?.instance?.let {
        StudyGroupScreen(it)
    }
}