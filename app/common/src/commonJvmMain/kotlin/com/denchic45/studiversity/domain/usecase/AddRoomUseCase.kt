package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.RoomRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.room.model.CreateRoomRequest
import com.denchic45.stuiversity.api.room.model.RoomResponse
import me.tatarka.inject.annotations.Inject

@Inject
class AddRoomUseCase(private val roomRepository: RoomRepository) {
    suspend operator fun invoke(request:CreateRoomRequest): Resource<RoomResponse> {
        return roomRepository.add(request)
    }
}