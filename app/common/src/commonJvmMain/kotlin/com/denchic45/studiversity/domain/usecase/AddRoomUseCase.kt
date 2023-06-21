package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.RoomRepository
import com.denchic45.studiversity.data.repository.SpecialtyRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.room.model.CreateRoomRequest
import com.denchic45.stuiversity.api.room.model.RoomResponse
import com.denchic45.stuiversity.api.specialty.model.CreateSpecialtyRequest
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class AddRoomUseCase(private val roomRepository: RoomRepository) {
    suspend operator fun invoke(request:CreateRoomRequest): Resource<RoomResponse> {
        return roomRepository.add(request)
    }
}