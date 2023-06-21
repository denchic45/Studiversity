package com.denchic45.studiversity.ui.search

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.usecase.FindRoomByContainsNameUseCase
import com.denchic45.studiversity.domain.usecase.FindSpecialtyByContainsNameUseCase
import com.denchic45.stuiversity.api.room.model.RoomResponse
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class RoomChooserComponent(
    private val findRoomByContainsNameUseCase: FindRoomByContainsNameUseCase,
    @Assisted
    override val onSelect: (RoomResponse) -> Unit,
    @Assisted
    val componentContext: ComponentContext,
) : ChooserComponent<RoomResponse>(componentContext) {
    override fun search(query: String): Flow<Resource<List<RoomResponse>>> {
        return findRoomByContainsNameUseCase(query)
    }
}