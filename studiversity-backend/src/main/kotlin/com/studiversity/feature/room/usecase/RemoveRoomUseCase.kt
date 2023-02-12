package com.studiversity.feature.room.usecase

import com.studiversity.feature.room.RoomRepository
import com.studiversity.transaction.TransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class RemoveRoomUseCase(
    private val transactionWorker: TransactionWorker,
    private val roomRepository: RoomRepository
) {
    operator fun invoke(roomId: UUID) = transactionWorker {
        if (!roomRepository.remove(roomId)) throw NotFoundException()
    }
}