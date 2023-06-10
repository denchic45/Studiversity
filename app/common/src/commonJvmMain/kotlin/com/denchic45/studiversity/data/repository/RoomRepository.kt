package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.data.fetchResourceFlow
import com.denchic45.studiversity.data.service.NetworkService
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.room.RoomApi
import com.denchic45.stuiversity.api.room.model.RoomResponse
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

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
}