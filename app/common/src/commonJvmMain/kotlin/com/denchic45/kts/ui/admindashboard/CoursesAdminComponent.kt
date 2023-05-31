package com.denchic45.kts.ui.admindashboard

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.ui.chooser.CourseChooserComponent
import com.denchic45.kts.ui.chooser.SearchableComponent
import com.denchic45.stuiversity.api.course.model.CourseResponse
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class CoursesAdminComponent(
    courseChooserComponent: (onSelect: (CourseResponse) -> Unit, ComponentContext) -> CourseChooserComponent,
    @Assisted
    onSelect: (CourseResponse) -> Unit,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext,
    SearchableComponent<CourseResponse> by courseChooserComponent(onSelect, componentContext) {

}