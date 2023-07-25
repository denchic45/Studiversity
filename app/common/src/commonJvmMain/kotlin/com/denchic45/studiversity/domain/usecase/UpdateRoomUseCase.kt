package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.RoomRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.room.model.RoomResponse
import com.denchic45.stuiversity.api.room.model.UpdateRoomRequest
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class UpdateRoomUseCase(private val roomRepository: RoomRepository) {
    suspend operator fun invoke(
        roomId: UUID,
        request: UpdateRoomRequest
    ): Resource<RoomResponse> {
        return roomRepository.update(roomId, request)
    }
}