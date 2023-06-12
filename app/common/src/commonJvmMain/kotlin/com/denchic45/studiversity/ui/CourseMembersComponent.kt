package com.denchic45.studiversity.ui

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.map
import com.denchic45.studiversity.domain.stateInResource
import com.denchic45.studiversity.domain.usecase.FindMembersByScopeUseCase
import com.denchic45.studiversity.ui.model.toUserItem
import com.denchic45.studiversity.util.componentScope
import com.denchic45.studiversity.util.map
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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

    private val _members = flow { emit(findMembersByScopeUseCase(courseId)) }
        .stateInResource(componentScope)

    val members = _members.map(componentScope) {it.map { it.map { it.user.toUserItem() } }}

    fun onMemberClick(memberId: UUID) {
        onMemberOpen(memberId)
    }
}