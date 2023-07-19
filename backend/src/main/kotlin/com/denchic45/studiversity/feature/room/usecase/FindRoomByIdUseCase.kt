package com.denchic45.studiversity.feature.room.usecase

import com.denchic45.studiversity.feature.room.RoomRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class FindRoomByIdUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val roomRepository: RoomRepository
) {
  suspend operator fun invoke(roomId: UUID) = suspendTransactionWorker {
        roomRepository.findById(roomId) ?: throw NotFoundException()
    }
}