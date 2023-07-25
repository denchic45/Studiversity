package com.denchic45.studiversity.ui.search

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.mapResource
import com.denchic45.studiversity.domain.usecase.FindUserByContainsNameUseCase
import com.denchic45.studiversity.ui.model.UserItem
import com.denchic45.studiversity.ui.model.toUserItem
import com.denchic45.stuiversity.api.user.model.UserResponse
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class UserChooserComponent(
    private val findUserByContainsNameUseCase: FindUserByContainsNameUseCase,
    @Assisted
    override val onSelect: (UserItem) -> Unit,
    @Assisted
    val componentContext: ComponentContext,
) : ChooserComponent<UserItem>(componentContext) {
    override fun search(query: String): Flow<Resource<List<UserItem>>> {
        return findUserByContainsNameUseCase(query).mapResource { it.map(UserResponse::toUserItem) }
    }
}