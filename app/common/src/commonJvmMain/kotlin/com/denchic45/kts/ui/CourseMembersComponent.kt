package com.denchic45.kts.ui

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindMembersByScopeUseCase
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.flow.flow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CourseMembersComponent(
    private val findMembersByScopeUseCase: FindMembersByScopeUseCase,
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val onMemberOpen: (memberId: UUID) -> Unit,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    val members = flow { emit(findMembersByScopeUseCase(courseId)) }
        .stateInResource(componentScope)

    fun onMemberClick(memberId: UUID) {
        onMemberOpen(memberId)
    }
}