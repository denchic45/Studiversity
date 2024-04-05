package com.denchic45.studiversity.ui.search

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.api.room.model.RoomResponse
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.usecase.FindRoomByContainsNameUseCase
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class RoomChooserComponent(
    private val findRoomByContainsNameUseCase: FindRoomByContainsNameUseCase,
    @Assisted
    override val onFinish: (RoomResponse?) -> Unit,
    @Assisted
    val componentContext: ComponentContext,
) : ChooserComponent<RoomResponse>(componentContext) {
    override fun search(query: String): Flow<Resource<List<RoomResponse>>> {
        return findRoomByContainsNameUseCase(query)
    }
}