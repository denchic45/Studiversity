package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.api.member.CreateMemberRequest
import com.denchic45.studiversity.api.studygroup.member.StudyGroupMemberApi
import com.denchic45.studiversity.data.fetchResource
import com.denchic45.studiversity.data.service.NetworkService
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class StudyGroupMemberRepository(
    override val networkService: NetworkService,
    private val studyGroupMemberApi: StudyGroupMemberApi,
) : NetworkServiceOwner {

    suspend fun findMembersByStudyGroupId(studyGroupId: UUID) = fetchResource {
        studyGroupMemberApi.getByStudyGroup(studyGroupId)
    }

    suspend fun addMember(studyGroupId: UUID, request: CreateMemberRequest) = fetchResource {
        studyGroupMemberApi.create(studyGroupId, request)
    }

    suspend fun removeMember(studyGroupId: UUID, memberId: UUID) = fetchResource {
        studyGroupMemberApi.delete(studyGroupId, memberId)
    }
}