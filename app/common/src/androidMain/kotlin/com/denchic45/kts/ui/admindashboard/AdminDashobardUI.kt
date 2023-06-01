package com.denchic45.kts.ui.admindashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.kts.R
import com.denchic45.kts.ui.appbar.AppBarState
import com.denchic45.kts.ui.appbar2.AppBarContent
import com.denchic45.kts.ui.appbar2.LocalAppBarState
import com.denchic45.kts.ui.theme.spacing
import com.denchic45.kts.ui.uiTextOf

@Composable
fun AdminDashboardScreen(component: AdminDashboardComponent) {
    val appBarState = LocalAppBarState.current


    val childStack by component.childStack.subscribeAsState()

    Children(component.childStack) {
        when(val child = it.instance) {
            AdminDashboardComponent.Child.None -> {
                LaunchedEffect(Unit) {
                    appBarState.content = AppBarContent(title = uiTextOf("Панель управления"))
                }
                Column {
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
                }
            }
            is AdminDashboardComponent.Child.Courses -> CoursesAdminScreen(child.component)
            is AdminDashboardComponent.Child.Users -> TODO()
        }
    }
}

@Composable
fun AdminListItem(
    title: String,
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
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