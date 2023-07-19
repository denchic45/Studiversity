package com.denchic45.studiversity.feature.room.usecase

import com.denchic45.studiversity.feature.room.RoomRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class RemoveRoomUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val roomRepository: RoomRepository
) {
  suspend operator fun invoke(roomId: UUID) = suspendTransactionWorker {
        if (!roomRepository.remove(roomId)) throw NotFoundException()
    }
}