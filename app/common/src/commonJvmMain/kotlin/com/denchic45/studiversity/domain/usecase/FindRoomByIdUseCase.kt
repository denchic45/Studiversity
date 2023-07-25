package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.RoomRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.room.model.RoomResponse
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class FindRoomByIdUseCase(private val roomRepository: RoomRepository) {
    operator fun invoke(roomId: UUID): Flow<Resource<RoomResponse>> {
        return roomRepository.findById(roomId)
    }
}