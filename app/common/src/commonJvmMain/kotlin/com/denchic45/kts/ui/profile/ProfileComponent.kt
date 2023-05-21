package com.denchic45.kts.ui.profile

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.data.pref.UserPreferences
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.flatMapResourceFlow
import com.denchic45.kts.domain.map
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.kts.domain.usecase.FindStudyGroupByIdUseCase
import com.denchic45.kts.domain.usecase.FindStudyGroupsUseCase
import com.denchic45.kts.domain.usecase.ObserveUserUseCase
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.util.toUUID
import com.denchic45.stuiversity.util.uuidOf
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class ProfileComponent(
    userPreferences: UserPreferences,
    observeUserUseCase: ObserveUserUseCase,
    findStudyGroupUseCase: FindStudyGroupsUseCase,
    findStudyGroupByIdUseCase: FindStudyGroupByIdUseCase,
    private val checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    @Assisted
    userId: UUID,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    private val userFlow = observeUserUseCase(userId)

    private val studyGroups = findStudyGroupUseCase(memberId = uuidOf(userId))

    private val capabilities = userFlow.flatMapResourceFlow { profileUser ->
        checkUserCapabilitiesInScopeUseCase(
            scopeId = profileUser.id,
            capabilities = listOf(Capability.WriteUser)
        )
    }

    val viewState: StateFlow<Resource<ProfileViewState>> =
        combine(userFlow, studyGroups, capabilities) { userRes, studyGroupsRes, capabilitiesRes ->
            capabilitiesRes.mapResource { capabilities ->
                studyGroupsRes.mapResource { studyGroups ->
                    userRes.map { user ->
                        user.toProfileViewState(
                            studyGroups,
                            capabilities.hasCapability(Capability.WriteUser),
                            userPreferences.id.toUUID() == user.id
                        )
                    }
                }
            }
        }.stateInResource(componentScope)

    fun onAvatarClick() {
        TODO("Not yet implemented")
    }

    fun onStudyGroupClick(studyGroupId: UUID) {

    }
}