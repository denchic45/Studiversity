package com.denchic45.kts.ui.admindashboard

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.denchic45.kts.ui.appbar.LocalAppBarInteractor
import com.denchic45.kts.ui.chooser.SearchScreen
import com.denchic45.stuiversity.api.course.model.CourseResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesAdminScreen(component: CoursesAdminComponent) {
    SearchScreen(
        component = component,
        appBarInteractor = LocalAppBarInteractor.current,
        keyItem = CourseResponse::id
    ) {
        Text(text = it.name)
    }
//    Column() {
//        var query by remember { mutableStateOf(component.query.value) }
//        SearchBar(
//            query = query,
//            onQueryChange = {
//                query = it
//                component.onQueryChange(it)
//            },
//            onSearch = { },
//            active = false,
//            onActiveChange = { },
//            modifier = Modifier,
//            leadingIcon = {
//                Icon(
//                    imageVector = Icons.Outlined.Search,
//                    contentDescription = "search"
//                )
//            }
//        ) {}
//    }
}