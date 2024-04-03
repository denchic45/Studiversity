package com.denchic45.studiversity.ui.coursemembers

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.usecase.FindAssignableRolesUseCase
import com.denchic45.studiversity.util.componentScope
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class EnrollCourseMembersComponent(
    private val findAssignableRolesUseCase: FindAssignableRolesUseCase,
    @Assisted
    private val courseId: UUID,
    @Assisted
    val onFinish: () -> Unit,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    private val componentScope = componentScope()


}