package com.denchic45.kts.data.repository

import com.denchic45.kts.data.fetchResource
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.room.RoomApi
import com.denchic45.stuiversity.api.room.model.RoomResponse
import me.tatarka.inject.annotations.Inject

@Inject
class RoomRepository(
    private val roomApi: RoomApi,
    override val networkService: NetworkService
) : NetworkServiceOwner, FindByContainsNameRepository<RoomResponse> {
    override suspend fun findByContainsName(text: String): Resource<List<RoomResponse>> {
        return fetchResource {
            roomApi.getList(text)
        }
    }
}