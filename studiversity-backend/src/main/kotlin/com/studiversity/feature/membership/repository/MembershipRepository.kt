package com.studiversity.feature.membership.repository

import com.studiversity.database.table.ExternalStudyGroupsMemberships
import com.studiversity.database.table.Memberships
import com.denchic45.stuiversity.api.membership.model.CreateMembershipRequest
import com.studiversity.logger.logger
import com.denchic45.stuiversity.util.toUUID
import io.github.jan.supabase.realtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class MembershipRepository(private val realtime: Realtime, private val coroutineScope: CoroutineScope) {

    private val membershipChannel: RealtimeChannel = realtime.createChannel("#membership")
    private val insertMembershipByGroupFlow =
        membershipChannel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
            table = "membership"
            filter = "type=eq.by_group"
        }.shareIn(coroutineScope, SharingStarted.Lazily)
    private val deleteMembershipByGroupFlow =
        membershipChannel.postgresChangeFlow<PostgresAction.Delete>(schema = "public") {
            table = "membership"
        }.filter { it.oldRecord.getValue("type").jsonPrimitive.content == "by_group" }
            .shareIn(coroutineScope, SharingStarted.Lazily)

    private val externalStudyGroupMembershipChannel = realtime.createChannel("#external_study_group_membership")
    private val insertStudyGroupExternalMembershipFlow =
        externalStudyGroupMembershipChannel.postgresChangeFlow<PostgresAction.Insert>("public") {
            table = "external_study_group_membership"
        }
    private val deleteStudyGroupExternalMembershipFlow =
        externalStudyGroupMembershipChannel.postgresChangeFlow<PostgresAction.Delete>("public") {
            table = "external_study_group_membership"
        }

    init {
        coroutineScope.launch {
            membershipChannel.join()
            externalStudyGroupMembershipChannel.join()
        }
    }

    fun addManualMembership(createMembershipRequest: CreateMembershipRequest) {
        Memberships.insert {
            it[scopeId] = createMembershipRequest.scopeId
            it[active] = true
            it[type] = createMembershipRequest.type
        }
    }

    private suspend fun listenMembershipsByScopeIds(scopeIds: List<UUID>): Flow<PostgresAction> {
        println("--Listening updates in memberships by scope_ids: $scopeIds")
        return scopeIds.map { scopeId ->
            val channel = realtime.createChannel("#studygroup_members")
            val membershipsByScopeIdFlow: Flow<PostgresAction> = channel.postgresChangeFlow(schema = "public") {
                table = "membership"
                filter = "scope_id=eq.${scopeId}"
            }
            channel.join()
            membershipsByScopeIdFlow
        }.merge().distinctUntilChanged()
    }

    fun observeMembershipsByScopeIds(scopeIds: List<UUID>) = flow {
        emit(getMembershipsByScopes(scopeIds))
        emitAll(
            listenMembershipsByScopeIds(scopeIds)
                .map { getMembershipsByScopes(scopeIds) }
        )
    }

    private fun getMembershipsByScopes(scopeIds: List<UUID>) = transaction {
        Memberships.select(Memberships.scopeId inList scopeIds)
            .map { it[Memberships.id].value }
    }

    fun findIdsByType(type: String): List<UUID> = transaction {
        Memberships.slice(Memberships.id).select(Memberships.type eq type).map { it[Memberships.id].value }
    }

    fun observeStudyGroupIdsOfExternalMembershipsByMembershipId(membershipId: UUID): StateFlow<List<UUID>> {
        val stateFlow = MutableStateFlow(transaction {
            ExternalStudyGroupsMemberships.select(
                ExternalStudyGroupsMemberships.membershipId eq membershipId
            ).map { it[ExternalStudyGroupsMemberships.studyGroupId].value }
        })

        coroutineScope.launch {
            launch {
                insertStudyGroupExternalMembershipFlow.filter {
                    it.record.getValue("membership_id").jsonPrimitive.content.toUUID() == membershipId
                }.map {
                    it.record.getValue("study_group_id").jsonPrimitive.content.toUUID()
                }.collect { addedStudyGroupId ->
                    logger.info { "added group to course; studyGroupId: $addedStudyGroupId" }
                    stateFlow.update { it + addedStudyGroupId }
                }
            }
            launch {
                deleteStudyGroupExternalMembershipFlow.filter {
                    it.oldRecord.getValue("membership_id").jsonPrimitive.content.toUUID() == membershipId
                }.map {
                    it.oldRecord.getValue("study_group_id").jsonPrimitive.content.toUUID()
                }.collect { removedStudyGroupId ->
                    logger.info { "removed group from course; studyGroupId: $removedStudyGroupId" }
                    stateFlow.update { it - removedStudyGroupId }
                }
            }
        }
        return stateFlow
    }

    fun findScopeIdByMembershipId(membershipId: UUID): UUID = transaction {
        Memberships.slice(Memberships.scopeId).select(Memberships.id eq membershipId).single()[Memberships.scopeId]
    }

    fun observeOnFirstAddExternalStudyGroupMembershipInMembership(): Flow<UUID> {
        return insertMembershipByGroupFlow.map {
            // Check if attached study group is first, else ignore her
            it.record.getValue("membership_id").jsonPrimitive.content.toUUID()
        }
    }

    fun observeRemoveExternalStudyGroupMemberships(): Flow<UUID> {
        return deleteMembershipByGroupFlow.map {
            // Check that there are no more attached groups left, else we ignore the deletion
            it.oldRecord.getValue("membership_id").jsonPrimitive.content.toUUID()
        }
    }

    fun findMembershipIdByTypeAndScopeId(type: String, scopeId: UUID): UUID {
        return Memberships.slice(Memberships.id)
            .select(Memberships.type eq type and (Memberships.scopeId eq scopeId))
            .first()[Memberships.id].value
    }

    fun findMembershipIdByTypeAndScopeId(scopeId: UUID, type: String?): UUID? {
        val query = Memberships.slice(Memberships.id).select(Memberships.scopeId eq scopeId)
        type?.let { query.andWhere { Memberships.type eq type } }
        return query.firstOrNull()?.get(Memberships.id)?.value
    }
}