package com.studiversity.feature.room.usecase

import com.studiversity.feature.room.RoomRepository
import com.studiversity.transaction.TransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class FindRoomByIdUseCase(
    private val transactionWorker: TransactionWorker,
    private val roomRepository: RoomRepository
) {
    operator fun invoke(roomId: UUID) = transactionWorker {
        roomRepository.findById(roomId) ?: throw NotFoundException()
    }
}