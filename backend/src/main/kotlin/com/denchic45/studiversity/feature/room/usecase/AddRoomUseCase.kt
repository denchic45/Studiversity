package com.denchic45.studiversity.feature.room.usecase

import com.denchic45.studiversity.feature.room.RoomRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.room.model.CreateRoomRequest

class AddRoomUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val roomRepository: RoomRepository
) {
  suspend operator fun invoke(createRoomRequest: CreateRoomRequest) = suspendTransactionWorker {
        roomRepository.add(createRoomRequest)
    }
}