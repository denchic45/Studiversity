package com.denchic45.studiversity.ui.coursemembers

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.map
import com.denchic45.studiversity.domain.mapResource
import com.denchic45.studiversity.domain.stateInResource
import com.denchic45.studiversity.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.studiversity.domain.usecase.FindMembersByScopeUseCase
import com.denchic45.studiversity.domain.usecase.RemoveMemberFromScopeUseCase
import com.denchic45.studiversity.ui.confirm.ConfirmDialogInteractor
import com.denchic45.studiversity.ui.confirm.ConfirmState
import com.denchic45.studiversity.ui.model.toUserItem
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.studiversity.util.componentScope
import com.denchic45.studiversity.util.map
import com.denchic45.stuiversity.api.role.model.Capability
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CourseMembersComponent(
    checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    private val findMembersByScopeUseCase: FindMembersByScopeUseCase,
    private val confirmDialogInteractor: ConfirmDialogInteractor,
    private val removeMemberFromScopeUseCase: RemoveMemberFromScopeUseCase,
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val onMemberOpen: (memberId: UUID) -> Unit,
    @Assisted
    private val onMemberEdit: (memberId: UUID) -> Unit,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    private val checkCapabilities = checkUserCapabilitiesInScopeUseCase(
        scopeId = courseId,
        capabilities = listOf(Capability.WriteCourse)
    ).stateInResource(componentScope)

    val allowEdit = checkCapabilities.mapResource {
        it.hasCapability(Capability.WriteCourse)
    }.stateInResource(componentScope)

    private val _members = flow { emit(findMembersByScopeUseCase(courseId)) }
        .stateInResource(componentScope)

    val members = _members.map(componentScope) { it.map { it.map { it.user.toUserItem() } } }

    fun onMemberClick(memberId: UUID) {
        onMemberOpen(memberId)
    }

    fun onMemberEditClick(memberId: UUID) {
        onMemberEdit(memberId)
    }

    fun onMemberRemoveClick(memberId: UUID) {
        componentScope.launch {
            if (confirmDialogInteractor.confirmRequest(
                    ConfirmState(
                        uiTextOf("Удалить учестника курса?"),
                        uiTextOf("Удалятся все данные, связанные с данным пользователем в курсе")
                    )
                )
            ) {
                removeMemberFromScopeUseCase(memberId, courseId)
            }
        }
    }
}