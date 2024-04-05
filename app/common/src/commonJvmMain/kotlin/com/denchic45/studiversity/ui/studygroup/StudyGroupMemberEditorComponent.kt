package com.denchic45.studiversity.ui.studygroup

import com.arkivanov.decompose.ComponentContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class StudyGroupMemberEditorComponent(
    @Assisted
    private val onFinish: () -> Unit,
    @Assisted
    private val studyGroupId: UUID,
    @Assisted
    private val userId: UUID,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
}