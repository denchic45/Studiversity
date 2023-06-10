package com.denchic45.studiversity.feature.room.usecase

import com.denchic45.studiversity.feature.room.RoomRepository
import com.denchic45.stuiversity.api.room.model.CreateRoomRequest
import com.denchic45.studiversity.transaction.TransactionWorker

class AddRoomUseCase(
    private val transactionWorker: TransactionWorker,
    private val roomRepository: RoomRepository
) {
    operator fun invoke(createRoomRequest: CreateRoomRequest) = transactionWorker {
        roomRepository.add(createRoomRequest)
    }
}