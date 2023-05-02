package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.RoomRepository
import com.denchic45.stuiversity.api.room.model.RoomResponse
import me.tatarka.inject.annotations.Inject

@Inject
class FindRoomByContainsNameUseCase(
    private val roomRepository: RoomRepository,
) : FindByContainsNameUseCase<RoomResponse>(roomRepository)