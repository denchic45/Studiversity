package com.denchic45.studiversity.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.systemRoleName
import com.denchic45.studiversity.ui.BlockContent
import com.denchic45.studiversity.ui.ScreenScaffold
import com.denchic45.studiversity.ui.component.HeaderItemUI
import com.denchic45.studiversity.ui.main.CustomAppBar
import com.denchic45.studiversity.ui.main.NavigationIconBack
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.ui.theme.toDrawablePath
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.seiko.imageloader.rememberAsyncImagePainter
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import java.util.*


@Composable
fun ProfileScreen(component: ProfileComponent) {
    val state by component.viewState.collectAsState()

    ScreenScaffold(
        topBar = {
            CustomAppBar(
                navigationContent = { NavigationIconBack(onClick = {}) },
                title = { Text("Профиль") }
            )
        }
    ) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            state.onSuccess { state ->
                ProfileContent(state)
                Row {
                    Column(
                        Modifier.width(496.dp)
                            .padding(
                                end = MaterialTheme.spacing.normal,
                                bottom = MaterialTheme.spacing.normal
                            )
                    ) { }
                    Column {
                        CoursesBlock(
                            courses = state.courses,
                            onCourseClick = component::onCourseClick,
                            onMoreCoursesClick = component::onMoreCourseClick
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun ProfileContent(state: ProfileViewState) {
    Column {
        BlockContent() {
            Row(
                Modifier.width(874.dp).height(232.dp).padding(horizontal = MaterialTheme.spacing.normal),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val user = state.user
                KamelImage(
                    resource = asyncPainterResource(user.avatarUrl),
                    null,
                    Modifier.size(168.dp).clip(CircleShape).background(Color.LightGray),
                    contentScale = ContentScale.Crop,

                    )
                Spacer(Modifier.width(MaterialTheme.spacing.normal))
                Column {
                    Text(
                        user.fullName,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(vertical = MaterialTheme.spacing.small)
                    )
                    Text(
                        state.role.systemRoleName(),
                        Modifier.padding(bottom = MaterialTheme.spacing.small),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Row {
                        Icon(
                            Icons.Outlined.Email,
                            null,
                            modifier = Modifier.padding(end = MaterialTheme.spacing.small)
                        )
                        Text(state.user.account.email, style = MaterialTheme.typography.titleSmall)
                    }

                    Spacer(Modifier.height(MaterialTheme.spacing.normal))

                    FilledTonalButton(onClick = {}) {
                        Text("Написать")
                    }

//                        if (profile.studyGroups.size == 1) {
//                            Row {
//                                Icon(Icons.Outlined.Groups, null)
//                                Spacer(Modifier.width(MaterialTheme.spacing.small))
//                                val studyGroup = profile.studyGroups.single()
//                                Text(studyGroup.name)
//                            }
//                        } else if (profile.studyGroups.size > 1) {
//                            // TODO: сделать диалоговое окно с несклькими группами
//                            Icon(Icons.Outlined.Groups, null)
//                            Text("${profile.studyGroups.size} групп")
//                        }
                }
            }
        }
        Spacer(Modifier.height(MaterialTheme.spacing.normal))

    }
}

@Composable
fun CoursesBlock(
    courses: List<CourseResponse>,
    onCourseClick: (courseId: UUID) -> Unit,
    onMoreCoursesClick: () -> Unit
) {
    BlockContent(Modifier.clickable(onClick = onMoreCoursesClick)) {
        HeaderItemUI("Курсы")
        courses.forEach { course ->
            ListItem(
                headlineContent = { Text(course.name) },
                modifier = Modifier.clickable { onCourseClick(course.id) })
        }
//        if (studyGroups.size == 1) {
//            Row {
//                Icon(Icons.Outlined.Groups, null)
//                Spacer(Modifier.width(MaterialTheme.spacing.small))
//                val studyGroup = studyGroups.single()
//                Text(studyGroup.name)
//            }
//        } else {
//            // TODO: сделать диалоговое окно с несклькими группами
//            Icon(Icons.Outlined.Groups, null)
//            Text("${studyGroups.size} групп")
//        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSideBar(
    modifier: Modifier,
    profileComponent: ProfileComponent,
    onCloseClick: () -> Unit,
) {
    val profileViewState by profileComponent.viewState.collectAsState()
    Column(modifier) {
        TopAppBar(
            title = {},
            actions = {
                IconButton(onCloseClick) {
                    Icon(Icons.Rounded.Close, null)
                }
            },
            colors = topAppBarColors(
                containerColor = Color.Transparent,
//        scrolledContainerColor = MaterialTheme.colorScheme.applyTonalElevation(
//    backgroundColor = containerColor,
//    elevation = TopAppBarSmallTokens.OnScrollContainerElevation
//)
            )
        )
        profileViewState.onSuccess { profile ->
            val user = profile.user
            ProfileHeader(user.avatarUrl, user.fullName)
//            if (profile.groupInfo != null) {
//                ListItem(Modifier.clickable(
//                    profile.groupClickable,
//                    onClick = profileComponent::onGroupClick
//                ),
//                    icon = { Icon(painterResource("ic_study_group".toDrawablePath()), null) },
//                    text = {
//                        Text(text = profile.groupInfo, style = MaterialTheme.typography.bodyLarge)
//                    })
//            }
            Divider(Modifier.fillMaxWidth())
            user.account.let { personalDate ->
                ListItem(
                    leadingContent = {
                        Icon(painterResource("ic_email".toDrawablePath()), null)
                    },
                    headlineContent = {
                        Text(personalDate.email, style = MaterialTheme.typography.bodyLarge)
                    },
                    supportingContent = { Text("Почта", style = MaterialTheme.typography.bodyMedium) })
            }
        }
    }
}

@Composable
fun ProfileHeader(photoUrl: String, title: String) {
    Row(
        modifier = Modifier.height(100.dp).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(photoUrl),
            null,
            Modifier.size(68.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Column(Modifier.padding(start = 16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
        }
    }
}