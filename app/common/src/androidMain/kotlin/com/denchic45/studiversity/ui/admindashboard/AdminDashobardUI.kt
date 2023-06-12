package com.denchic45.studiversity.ui.admindashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.studiversity.common.R
import com.denchic45.studiversity.ui.SyncedEffect
import com.denchic45.studiversity.ui.appbar2.AppBarContent
import com.denchic45.studiversity.ui.appbar2.LocalAppBarState
import com.denchic45.studiversity.ui.appbar2.updateAppBarState
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.ui.timetableLoader.TimetableLoaderScreen
import com.denchic45.studiversity.ui.timetablefinder.TimetableFinderScreen
import com.denchic45.studiversity.ui.uiTextOf

@Composable
fun AdminDashboardScreen(component: AdminDashboardComponent) {
    val appBarState = LocalAppBarState.current

    val childStack by component.childStack.subscribeAsState()
    Column {
        AdminListItem(
            title = "Расписания",
            subtitle = "Просмотр и редактирование",
            painter = painterResource(id = R.drawable.ic_timetable),
            contentDescription = "timetables",
            onClick = component::onTimetableFinderClick
        )

        AdminListItem(
            title = "Новое расписание",
            subtitle = "Создать с нуля или загрузить из документа",
            painter = rememberVectorPainter(Icons.Outlined.AddBox),
            contentDescription = "timetables",
            onClick = component::onTimetableLoaderClick
        )

        AdminListItem(
            title = "Курсы",
            painter = painterResource(id = R.drawable.ic_course),
            contentDescription = "courses",
            onClick = component::onCoursesClick
        )

        AdminListItem(
            title = "Пользователи",
            painter = painterResource(id = R.drawable.ic_user),
            contentDescription = "users",
            onClick = component::onUsersClick
        )

        AdminListItem(
            title = "Учебные группы",
            painter = painterResource(id = R.drawable.ic_study_group),
            contentDescription = "study groups",
            onClick = component::onStudyGroupsClick
        )

        AdminListItem(
            title = "Предметы",
            painter = painterResource(id = R.drawable.ic_subject),
            contentDescription = "subjects",
            onClick = component::onSubjectsClick
        )

        AdminListItem(
            title = "Специальности",
            painter = painterResource(id = R.drawable.ic_specialty),
            contentDescription = "specialties",
            onClick = component::onSpecialtiesClick
        )
    }
    Children(component.childStack) {
        when (val child = it.instance) {
            AdminDashboardComponent.Child.None -> {
                updateAppBarState( AppBarContent(title = uiTextOf("Панель управления")))

            }

            is AdminDashboardComponent.Child.TimetableFinder -> TimetableFinderScreen(child.component)
            is AdminDashboardComponent.Child.TimetableLoader -> TimetableLoaderScreen(child.component)
            is AdminDashboardComponent.Child.Courses -> CoursesAdminScreen(child.component)
            is AdminDashboardComponent.Child.Users -> UsersAdminScreen(child.component)
            is AdminDashboardComponent.Child.StudyGroups -> StudyGroupsAdminScreen(child.component)
            is AdminDashboardComponent.Child.Subjects -> SubjectsAdminScreen(child.component)
            is AdminDashboardComponent.Child.Specialties -> SpecialtiesAdminScreen(child.component)

        }
    }
}

@Composable
fun AdminListItem(
    title: String,
    subtitle: String? = null,
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
        },
        supportingContent = subtitle?.let {
            {
                Text(text = subtitle)
            }
        },
        leadingContent = {
            Icon(
                painter = painter,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(32.dp)
            )
        },
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = MaterialTheme.spacing.extraSmall)
    )
}