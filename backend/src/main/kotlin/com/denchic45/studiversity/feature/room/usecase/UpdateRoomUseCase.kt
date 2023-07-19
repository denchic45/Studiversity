package com.denchic45.studiversity.feature.room.usecase

import com.denchic45.studiversity.feature.room.RoomRepository
import com.denchic45.stuiversity.api.room.model.UpdateRoomRequest
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class UpdateRoomUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val roomRepository: RoomRepository
) {
  suspend operator fun invoke(roomId: UUID, updateRoomRequest: UpdateRoomRequest) = suspendTransactionWorker {
        roomRepository.update(roomId, updateRoomRequest) ?: throw NotFoundException()
    }
}