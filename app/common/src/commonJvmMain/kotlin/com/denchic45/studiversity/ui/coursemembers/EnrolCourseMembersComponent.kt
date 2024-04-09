package com.denchic45.studiversity.ui.coursemembers

import androidx.compose.runtime.*
import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.api.role.model.Role
import com.denchic45.studiversity.domain.model.UserItem
import com.denchic45.studiversity.domain.usecase.FindAssignableRolesUseCase
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.flow.MutableStateFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class EnrolCourseMembersComponent(
    private val findAssignableRolesUseCase: FindAssignableRolesUseCase,
    @Assisted
    private val onFinish: (Boolean) -> Unit,
    @Assisted
    private val courseId: UUID,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    private val componentScope = componentScope()


    val state = EnrolMembersState()
    private val queryText = MutableStateFlow("")
}

@Stable
class EnrolMembersState {
    var queryText by mutableStateOf("")
    var foundMembers by mutableStateOf(emptyList<UserItem>())
    var assignableRoles by mutableStateOf(listOf<Role>())

    val members = mutableStateListOf<UserItem>()
    var role by mutableStateOf<Role?>(null)

    val allowSave: Boolean
        get() = members.isNotEmpty() && role != null
}
