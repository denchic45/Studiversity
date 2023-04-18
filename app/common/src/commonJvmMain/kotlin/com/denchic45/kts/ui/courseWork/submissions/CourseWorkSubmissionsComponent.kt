package com.denchic45.kts.ui.courseWork.submissions

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.util.componentScope
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CourseWorkSubmissionsComponent(
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val elementId: UUID,
    @Assisted
    private val componentContext: ComponentContext
):ComponentContext by componentContext {
    private val componentScope = componentScope()

}
