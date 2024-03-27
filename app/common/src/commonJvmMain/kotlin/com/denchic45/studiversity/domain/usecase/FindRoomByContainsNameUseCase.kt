package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.RoomRepository
import com.denchic45.studiversity.api.room.model.RoomResponse
import me.tatarka.inject.annotations.Inject

@Inject
class FindRoomByContainsNameUseCase(
    roomRepository: RoomRepository,
) : FindByContainsNameUseCase<RoomResponse>(roomRepository)