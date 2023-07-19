package com.denchic45.studiversity.feature.room.usecase

import com.denchic45.studiversity.feature.room.RoomRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.studiversity.util.searchable

class SearchRoomsUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val roomRepository: RoomRepository
) {
  suspend operator fun invoke(query: String) = suspendTransactionWorker {
        roomRepository.find(query.searchable())
    }
}