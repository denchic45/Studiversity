package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.data.fetchResource
import com.denchic45.studiversity.data.fetchResourceFlow
import com.denchic45.studiversity.data.service.NetworkService
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.room.RoomApi
import com.denchic45.stuiversity.api.room.model.CreateRoomRequest
import com.denchic45.stuiversity.api.room.model.RoomResponse
import com.denchic45.stuiversity.api.room.model.UpdateRoomRequest
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class RoomRepository(
    private val roomApi: RoomApi,
    override val networkService: NetworkService,
) : NetworkServiceOwner, FindByContainsNameRepository<RoomResponse> {
    override fun findByContainsName(text: String): Flow<Resource<List<RoomResponse>>> {
        return fetchResourceFlow {
            roomApi.getList(text)
        }
    }

    fun findById(roomId: UUID) = fetchResourceFlow {
        roomApi.getById(roomId)
    }

    suspend fun add(request: CreateRoomRequest) = fetchResource {
        roomApi.create(request)
    }

    suspend fun update(
        roomId: UUID,
        request: UpdateRoomRequest
    ) = fetchResource {
        roomApi.update(roomId, request)
    }

    suspend fun remove(roomId: UUID) = fetchResource {
        roomApi.delete(roomId)
    }
}