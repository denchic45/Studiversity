package com.denchic45.kts.ui.chooser

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.usecase.FindUserByContainsNameUseCase
import com.denchic45.kts.ui.model.UserItem
import com.denchic45.kts.ui.model.toUserItem
import com.denchic45.stuiversity.api.user.model.UserResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class UserChooserComponent(
    private val findUserByContainsNameUseCase: FindUserByContainsNameUseCase,
    @Assisted
    override val onFinish: (UserItem?) -> Unit,
    @Assisted
    val componentContext: ComponentContext,
) : ChooserComponent<UserItem>(componentContext) {
    override fun search(query: String): Flow<Resource<List<UserItem>>> {
        return findUserByContainsNameUseCase(query).mapResource { it.map(UserResponse::toUserItem) }
    }
}