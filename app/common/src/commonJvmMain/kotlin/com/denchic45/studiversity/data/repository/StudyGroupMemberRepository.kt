package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.api.member.CreateMemberRequest
import com.denchic45.studiversity.api.studygroup.member.StudyGroupMemberApi
import com.denchic45.studiversity.data.db.local.source.StudyGroupLocalDataSource
import com.denchic45.studiversity.data.fetchResource
import com.denchic45.studiversity.data.fetchResourceFlow
import com.denchic45.studiversity.data.observeResource
import com.denchic45.studiversity.data.service.NetworkService
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class StudyGroupMemberRepository(
    override val networkService: NetworkService,
    private val studyGroupMemberApi: StudyGroupMemberApi,
    private val studyGroupLocalDataSource: StudyGroupLocalDataSource
) : NetworkServiceOwner {

    fun findMembersByStudyGroupId(studyGroupId: UUID) = fetchResourceFlow {
        studyGroupMemberApi.getByStudyGroup(studyGroupId)
    }

    suspend fun observeMembersByStudyGroupId(studyGroupId: UUID) = observeResource(
        query = studyGroupLocalDataSource.observe(studyGroupId.toString()),
        fetch = { studyGroupMemberApi.getByStudyGroup(studyGroupId) },
        saveFetch = { studyGroupLocalDataSource.upsertMembers(studyGroupId.toString(), it) }
    )

    suspend fun addMember(studyGroupId: UUID, request: CreateMemberRequest) = fetchResource {
        studyGroupMemberApi.create(studyGroupId, request)
    }

    suspend fun removeMember(studyGroupId: UUID, memberId: UUID) = fetchResource {
        studyGroupMemberApi.delete(studyGroupId, memberId)
    }
}