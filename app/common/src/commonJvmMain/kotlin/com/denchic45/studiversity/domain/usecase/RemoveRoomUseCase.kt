package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.RoomRepository
import com.denchic45.studiversity.data.repository.SpecialtyRepository
import com.denchic45.studiversity.domain.EmptyResource
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
import com.denchic45.stuiversity.api.specialty.model.UpdateSpecialtyRequest
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class RemoveRoomUseCase(private val roomRepository: RoomRepository) {
    suspend operator fun invoke(
        specialtyId: UUID
    ): EmptyResource {
        return roomRepository.remove(specialtyId)
    }
}