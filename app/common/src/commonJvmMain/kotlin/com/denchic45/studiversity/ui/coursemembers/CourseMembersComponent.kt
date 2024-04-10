package com.denchic45.studiversity.ui.coursemembers

import androidx.compose.runtime.Immutable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.*
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.value.Value
import com.denchic45.studiversity.api.role.model.Capability
import com.denchic45.studiversity.api.role.model.Role
import com.denchic45.studiversity.domain.model.UserItem
import com.denchic45.studiversity.domain.model.toUserItem
import com.denchic45.studiversity.domain.resource.*
import com.denchic45.studiversity.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.studiversity.domain.usecase.FindCourseMembersUseCase
import com.denchic45.studiversity.domain.usecase.RemoveCourseMemberUseCase
import com.denchic45.studiversity.ui.membereditor.MemberRolesEditorComponent
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.navigator.RootNavigator
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class CourseMembersComponent(
    checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    findCourseMembersUseCase: FindCourseMembersUseCase,
    private val removeCourseMemberUseCase: RemoveCourseMemberUseCase,
    private val enrolCourseMembersComponent: (
            (Boolean) -> Unit, UUID, ComponentContext
    ) -> EnrolCourseMembersComponent,
    private val memberRolesEditorComponent: (
            (Boolean) -> Unit, UUID, UserItem, ComponentContext
    ) -> MemberRolesEditorComponent,
    @Assisted
    private val rootNavigator: RootNavigator,
    @Assisted
    private val courseId: UUID,
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

    private val slotNavigation = SlotNavigation<SlotConfig>()
    val childSlot: Value<ChildSlot<SlotConfig, SlotChild>> = childSlot(
        source = slotNavigation,
        serializer = SlotConfig.serializer(),
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                is SlotConfig.EnrolMembers -> SlotChild.EnrolMembers(
                    enrolCourseMembersComponent(
                        { invited ->
                            slotNavigation.dismiss()
                            if (invited) forceUpdate()
                        }, courseId,
                        context
                    )
                )

                is SlotConfig.MemberEditor -> SlotChild.MemberEditor(
                    memberRolesEditorComponent(
                        { edited ->
                            slotNavigation.dismiss()
                            if (edited) forceUpdate()
                        },
                        courseId,
                        config.user,
                        context
                    )
                )
            }
        }
    )

    val refreshing = MutableStateFlow(false)
    val members: MutableStateFlow<Resource<CourseMembersState>> = MutableStateFlow(loadingResource())

    private val observedMembers = findCourseMembersUseCase(courseId)
        .mapResource { scopeMembers ->
            val teachers = scopeMembers.filter { member -> Role.Teacher in member.roles }
            val students = scopeMembers.filter { member -> Role.Student in member.roles }
            CourseMembersState(
                teachers = teachers.map { it.user.toUserItem() },
                students = students.map { it.user.toUserItem() },
                otherMembers = (scopeMembers - teachers.toSet() - students.toSet()).map { it.user.toUserItem() }
            )
        }

    init {
        componentScope.launch { members.emitAll(observedMembers) }

        refreshing.filter { it }.onEach { forceUpdate() }
            .launchIn(componentScope)

        members.drop(1).onEach { refreshing.value = false }
            .launchIn(componentScope)
    }

    private fun forceUpdate() {
        componentScope.launch { members.value = observedMembers.last() }
    }

    fun onMemberClick(memberId: UUID) {
        rootNavigator.bringToFront(RootConfig.Profile(memberId))
    }

    fun onMemberEditClick(member: UserItem) {
        slotNavigation.activate(SlotConfig.MemberEditor(member))
    }

    fun onMemberRemoveClick(memberId: UUID) {
        componentScope.launch {
            removeCourseMemberUseCase(courseId, memberId)
                .onSuccess { forceUpdate() }
        }
    }

    @Serializable
    sealed class SlotConfig {
        @Serializable
        data object EnrolMembers : SlotConfig()

        @Serializable
        data class MemberEditor(val user: UserItem) : SlotConfig()
    }

    sealed class SlotChild {
        class EnrolMembers(val component: EnrolCourseMembersComponent) : SlotChild()

        class MemberEditor(val component: MemberRolesEditorComponent) : SlotChild()
    }
}

@Immutable
data class CourseMembersState(
    val teachers: List<UserItem>,
    val students: List<UserItem>,
    val otherMembers: List<UserItem>
) {
    fun isEmpty(): Boolean {
        return teachers.isEmpty() && students.isEmpty() && otherMembers.isEmpty()
    }
}