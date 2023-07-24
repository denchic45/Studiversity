package com.denchic45.studiversity.ui.admindashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.search.SearchState
import com.denchic45.studiversity.ui.search.SearchableComponent
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.ui.theme.toDrawablePath

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(component: AdminDashboardComponent) {
    val childStack by component.childStack.subscribeAsState()

    Row {
        ModalDrawerSheet(Modifier.requiredWidth(300.dp)) {
            AdminListItem(
                title = "Расписания",
                selected = childStack.active.instance is AdminDashboardComponent.Child.TimetableFinder,
                painter = painterResource("ic_timetable".toDrawablePath()),
                contentDescription = "timetables",
                onClick = component::onTimetableFinderClick
            )

            AdminListItem(
                title = "Создать расписание",
                selected = childStack.active.instance is AdminDashboardComponent.Child.TimetableLoader,
                painter = rememberVectorPainter(Icons.Default.AddBox),
                contentDescription = "timetables",
                onClick = component::onTimetableLoaderClick
            )

            AdminListItem(
                title = "Курсы",
                selected = childStack.active.instance is AdminDashboardComponent.Child.Courses,
                painter = painterResource("ic_course".toDrawablePath()),
                contentDescription = "courses",
                onClick = component::onCoursesClick
            )

            AdminListItem(
                title = "Пользователи",
                selected = childStack.active.instance is AdminDashboardComponent.Child.Users,
                painter = painterResource("ic_user".toDrawablePath()),
                contentDescription = "users",
                onClick = component::onUsersClick
            )

            AdminListItem(
                title = "Учебные группы",
                selected = childStack.active.instance is AdminDashboardComponent.Child.StudyGroups,
                painter = painterResource("ic_study_group".toDrawablePath()),
                contentDescription = "study groups",
                onClick = component::onStudyGroupsClick
            )

            AdminListItem(
                title = "Предметы",
                selected = childStack.active.instance is AdminDashboardComponent.Child.Subjects,
                painter = painterResource("ic_subject".toDrawablePath()),
                contentDescription = "subjects",
                onClick = component::onSubjectsClick
            )

            AdminListItem(
                title = "Специальности",
                selected = childStack.active.instance is AdminDashboardComponent.Child.Specialties,
                painter = painterResource("ic_specialty".toDrawablePath()),
                contentDescription = "specialties",
                onClick = component::onSpecialtiesClick
            )
        }

        Children(component.childStack) {
            when (val child = it.instance) {
                AdminDashboardComponent.Child.None -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Выберите раздел")
                    }
                }

                is AdminDashboardComponent.Child.TimetableFinder -> TODO()
                is AdminDashboardComponent.Child.TimetableLoader -> TODO()
                is AdminDashboardComponent.Child.Courses -> CoursesAdminScreen(child.component)
                is AdminDashboardComponent.Child.Users -> UsersAdminScreen(child.component)
                is AdminDashboardComponent.Child.StudyGroups -> StudyGroupsAdminScreen(child.component)
                is AdminDashboardComponent.Child.Subjects -> SubjectsAdminScreen(child.component)
                is AdminDashboardComponent.Child.Specialties -> TODO()
                is AdminDashboardComponent.Child.Rooms -> TODO()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminListItem(
    title: String,
    selected: Boolean,
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
) {
    NavigationDrawerItem(
        label = { Text(text = title, style = MaterialTheme.typography.labelLarge) },
        onClick = onClick,
        selected = selected
    )
}

@Composable
fun <T> AdminSearchScreen(
    component: SearchableAdminComponent<T>,
    keyItem: (T) -> Any,
    emptyQueryContent: (@Composable () -> Unit)? = { StartSearch() },
    emptyResultContent: (@Composable () -> Unit)? = { EmptySearch() },
    placeholder: String = "Поиск",
    itemContent: @Composable (T) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        SearchContent(
            component.chooserComponent,
            keyItem,
            Modifier.width(500.dp),
            emptyQueryContent,
            emptyResultContent,
            placeholder,
            component::onAddClick,
            itemContent
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun <T> SearchContent(
    component: SearchableComponent<T>,
    keyItem: (T) -> Any,
    modifier: Modifier,
    emptyQueryContent: @Composable (() -> Unit)?,
    emptyResultContent: @Composable (() -> Unit)?,
    placeholder: String,
    onAddClick: () -> Unit,
    itemContent: @Composable (T) -> Unit,
) {
    Column(modifier) {
        var query by remember { mutableStateOf(component.query.value) }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    component.onQueryChange(it)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "search"
                    )
                }
            )
            Spacer(Modifier.width(MaterialTheme.spacing.normal))
            IconButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, "add")
            }
        }

        SearchedItemsContent(component, keyItem, emptyQueryContent, emptyResultContent, itemContent)
    }
}

@Composable
fun <T> SearchedItemsContent(
    component: SearchableComponent<T>,
    keyItem: (T) -> Any,
    emptyQueryContent: @Composable (() -> Unit)?,
    emptyResultContent: (@Composable () -> Unit)?,
    itemContent: @Composable (T) -> Unit,
) {
    val searchState by component.searchState.collectAsState()

    when (val state = searchState) {
        SearchState.EmptyQuery -> if (emptyQueryContent != null) {
            emptyQueryContent()
        }

        is SearchState.Result -> {
            ResourceContent(resource = state.items) { items ->
                if (items.isNotEmpty()) {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            top = 64.dp,
                            bottom = MaterialTheme.spacing.medium
                        )
                    ) {
                        items(items, key = keyItem) {
                            Box(modifier = Modifier.clickable { component.onItemClick(it) }) {
                                itemContent(it)
                            }
                        }
                    }
                } else {
                    emptyResultContent?.let { emptyResultContent() }
                }
            }
        }
    }
}


@Composable
fun StartSearch() {
    IconTitle(icon = {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "search",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(78.dp)
        )
    }, title = {
        Text(text = "Начните искать")
    })

}

@Composable
fun EmptySearch() {
    IconTitle(
        icon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "not found",
                tint = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(78.dp)
            )
        }, title = { Text(text = "Ничего не найдено") }
    )
}

@Composable
fun IconTitle(
    icon: @Composable () -> Unit,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            icon()
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            title()
        }
    }
}