package com.studiversity.feature.room.usecase

import com.studiversity.feature.room.RoomRepository
import com.stuiversity.api.room.model.UpdateRoomRequest
import com.studiversity.transaction.TransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class UpdateRoomUseCase(
    private val transactionWorker: TransactionWorker,
    private val roomRepository: RoomRepository
) {
    operator fun invoke(roomId: UUID, updateRoomRequest: UpdateRoomRequest) = transactionWorker {
        roomRepository.update(roomId, updateRoomRequest) ?: throw NotFoundException()
    }
}