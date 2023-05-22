package com.studiversity.feature.membership.repository

import com.denchic45.stuiversity.api.membership.model.Member
import com.denchic45.stuiversity.api.membership.model.MembershipResponse
import com.denchic45.stuiversity.api.membership.model.ScopeMember
import com.denchic45.stuiversity.api.role.model.Role
import com.denchic45.stuiversity.api.user.model.Account
import com.denchic45.stuiversity.api.user.model.UserResponse
import com.denchic45.stuiversity.util.toUUID
import com.studiversity.database.exists
import com.studiversity.database.table.*
import com.studiversity.feature.role.mapper.toRole
import com.studiversity.logger.logger
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.createChannel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.notInList
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*

class UserMembershipRepository(
    coroutineScope: CoroutineScope,
    realtime: Realtime
) {
    private val userMembershipChannel = realtime.createChannel("#user_membership")
    private val insertUserMembershipFlow =
        userMembershipChannel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
            table = "user_membership"
        }.shareIn(coroutineScope, SharingStarted.Lazily)

    private val deleteUserMembershipFlow =
        userMembershipChannel.postgresChangeFlow<PostgresAction.Delete>(schema = "public") {
            table = "user_membership"
        }.shareIn(coroutineScope, SharingStarted.Lazily)

    init {
        coroutineScope.launch {
            userMembershipChannel.join()
        }
    }

    fun addMember(member: Member) = transaction {
        UsersMemberships.insert {
            it[memberId] = member.userId
            it[membershipId] = member.membershipId
        }
    }

    fun removeMember(member: Member) = transaction {
        UsersMemberships.deleteWhere {
            memberId eq member.userId and (membershipId eq member.membershipId)
        }
        removeUsersRolesWhoNotExistInAnyMembershipByScopeId(
            listOf(member.userId),
            MembershipDao.findById(member.membershipId)!!.scopeId
        )
    }

    fun findMembersByScope(scopeId: UUID) = Memberships
        .innerJoin(UsersMemberships, { Memberships.id }, { membershipId })
        .innerJoin(Users, { UsersMemberships.memberId }, { Users.id })
        .select(Memberships.scopeId eq scopeId)
        .groupBy { it[UsersMemberships.memberId] }
        .map { (userId, rows) ->
            ScopeMember(
                user = UserResponse(
                    id = rows.first()[Users.id].value,
                    firstName = rows.first()[Users.firstName],
                    surname = rows.first()[Users.surname],
                    patronymic = rows.first()[Users.patronymic],
                    account = Account(rows.first()[Users.email]),
                    avatarUrl = rows.first()[Users.avatarUrl],
                    generatedAvatar = rows.first()[Users.generatedAvatar],
                    gender = rows.first()[Users.gender]
                ),
                scopeId = scopeId,
                membershipIds = rows.map { it[UsersMemberships.membershipId].value },
                roles = UserRoleScopeDao.find(
                    UsersRolesScopes.scopeId eq scopeId and (UsersRolesScopes.userId eq userId)
                ).map { it.role.toRole() }
            )
        }

    fun findMemberIdsByScopeAndRole(scopeId: UUID, roleId: Long) = MembershipsInnerUserMembershipsInnerUsersRolesScopes
        .select(Memberships.scopeId eq scopeId and (UsersRolesScopes.roleId eq roleId))
        .map { it[UsersMemberships.memberId].value }

    fun findMemberByScope(userId: UUID, scopeId: UUID) = Memberships
        .innerJoin(UsersMemberships, { Memberships.id }, { membershipId })
        .innerJoin(Users, { UsersMemberships.memberId }, { Users.id })
        .select(Memberships.scopeId eq scopeId and (Users.id eq userId))
        .let { rows ->
            ScopeMember(
                user = UserResponse(
                    id = rows.first()[Users.id].value,
                    firstName = rows.first()[Users.firstName],
                    surname = rows.first()[Users.surname],
                    patronymic = rows.first()[Users.patronymic],
                    account = Account(rows.first()[Users.email]),
                    avatarUrl = rows.first()[Users.avatarUrl],
                    generatedAvatar = rows.first()[Users.generatedAvatar],
                    gender = rows.first()[Users.gender]
                ),
                scopeId = scopeId,
                membershipIds = rows.map { it[UsersMemberships.membershipId].value },
                roles = UserRoleScopeDao.find(UsersRolesScopes.scopeId eq scopeId and (UsersRolesScopes.userId eq userId))
                    .map { it.role.toRole() }
            )
        }

    private fun removeUsersRolesWhoNotExistInAnyMembershipByScopeId(userIds: List<UUID>, scopeId: UUID) {
        findUsersWhoHasRolesAndNotExistInAnyMembershipByScopeId(userIds, scopeId)
            .let { missingMemberIds ->
                UsersRolesScopes.deleteWhere {
                    UsersRolesScopes.scopeId eq scopeId and (userId inList missingMemberIds)
                }
            }
    }

    fun findUsersWhoHasRolesAndNotExistInAnyMembershipByScopeId(
        userIds: List<UUID>,
        scopeId: UUID
    ) = Memberships.innerJoin(UsersMemberships, { Memberships.id }, { membershipId })
        .rightJoin(
            UsersRolesScopes,
            { Memberships.scopeId },
            { UsersRolesScopes.scopeId },
            { UsersRolesScopes.userId eq UsersMemberships.memberId })
        .select(
            UsersRolesScopes.userId inList userIds
                    and (UsersRolesScopes.scopeId eq scopeId)
                    and (UsersMemberships.memberId.isNull())
        ).map { it[UsersRolesScopes.userId] }

    fun findAndAddMissingStudentsOfGroupsToCourse(groupIds: List<UUID>, courseMembershipId: UUID) = transaction {
        addMembersToMembership(
            memberIds = findMissingStudentsFromGroupsToCourse(courseMembershipId, groupIds),
            membershipId = courseMembershipId,
            roleId = Role.Student.id
        )
    }

    fun findMissingStudentsFromGroupsToCourse(courseMembershipId: UUID, groupIds: List<UUID>) = transaction {
        val courseStudentsFromGroups = UsersMemberships.slice(UsersMemberships.memberId)
            .select(UsersMemberships.membershipId eq courseMembershipId)
            .map { it[UsersMemberships.memberId] }

        val studentWithChildRoles: List<Long> = RoleDao.findChildRoleIdsByRoleId(Role.Student.id) + Role.Student.id

        Memberships.innerJoin(UsersMemberships, { Memberships.id }, { membershipId })
            .innerJoin(
                UsersRolesScopes,
                { Memberships.scopeId },
                { scopeId },
                { UsersRolesScopes.userId eq UsersMemberships.memberId })
            .slice(UsersMemberships.memberId)
            .select(
                UsersRolesScopes.roleId inList studentWithChildRoles
                        and (Memberships.scopeId inList groupIds)
                        and (UsersMemberships.memberId notInList courseStudentsFromGroups)
            ).distinctBy { it[UsersMemberships.memberId] }
            .map { it[UsersMemberships.memberId].value }
    }

    fun findAndRemoveRemainingStudentsOfCourseToGroups(groupIds: List<UUID>, courseMembershipId: UUID) = transaction {
        removeMembersFromMembership(
            memberIds = findRemainingStudentsOfCourseFromGroups(groupIds, courseMembershipId),
            membershipId = courseMembershipId
        )
    }

    fun findRemainingStudentsOfCourseFromGroups(groupIds: List<UUID>, courseMembershipId: UUID) = transaction {
        val studentWithChildRoles: List<Long> = RoleDao.findChildRoleIdsByRoleId(Role.Student.id) + Role.Student.id

        val groupStudentIds = Memberships.innerJoin(UsersMemberships, { Memberships.id }, { membershipId })
            .innerJoin(
                UsersRolesScopes,
                { Memberships.scopeId },
                { scopeId },
                { UsersRolesScopes.userId eq UsersMemberships.memberId })
            .slice(UsersMemberships.memberId)
            .select(
                Memberships.scopeId inList groupIds
                        and (UsersRolesScopes.roleId inList studentWithChildRoles)
            ).map { it[UsersMemberships.memberId] }

        UsersMemberships.slice(UsersMemberships.memberId)
            .select(
                UsersMemberships.membershipId eq courseMembershipId
                        and (UsersMemberships.memberId notInList groupStudentIds)
            ).distinctBy { it[UsersMemberships.memberId] }
            .map { it[UsersMemberships.memberId].value }
    }

    private fun findMembershipsByScopeIds(groupIds: List<UUID>): List<UUID> {
        return UsersMemberships.innerJoin(Memberships,
            { membershipId },
            { Memberships.id },
            { Memberships.scopeId inList groupIds })
            .slice(UsersMemberships.membershipId)
            .selectAll().map { it[UsersMemberships.membershipId].value }
    }

    /**
     * Find members who are exist in one of the membership sources but not exist in target membership.
     *
     * Skips members who are in the target membership and in one of the membership sources.
     *
     * Usage example: find members of group who are still not enrolled in the course
     * @param membershipIdsSources membership sources where all desired members
     * @param membershipIdTarget membership target where can be unrelated members
     * @return user ids who exist in one of the membership sources but not exist in membership target
     */
    fun findUnrelatedMembersByManyToOneMemberships(
        membershipIdsSources: List<UUID>,
        membershipIdTarget: UUID
    ) = transaction {
        val sourceUm = UsersMemberships.alias("sourceUm")
        val targetUm = UsersMemberships.alias("targetUm")
        sourceUm.leftJoin(
            otherTable = targetUm,
            onColumn = { sourceUm[UsersMemberships.memberId] },
            otherColumn = { targetUm[UsersMemberships.memberId] },
            additionalConstraint = { targetUm[UsersMemberships.membershipId] eq membershipIdTarget }
        )
            .slice(sourceUm[UsersMemberships.memberId])
            .select(
                targetUm[UsersMemberships.membershipId].isNull()
                        and (sourceUm[UsersMemberships.membershipId] inList membershipIdsSources)
            ).map { it[sourceUm[UsersMemberships.memberId]].value }
    }

    /**
     * Find and add members to target membership who are exist in one of the membership sources but not exist in membership target.
     *
     * Finding members using [findUnrelatedMembersByManyToOneMemberships]
     *
     * Usage example: find members of group who are still not enrolled in the course and enroll them
     * @param membershipIdsSources membership sources where all desired members
     * @param membershipIdTarget membership target where can be unrelated members
     */
    fun findAndAddUnrelatedMembersByManyToOneMemberships(
        membershipIdsSources: List<UUID>,
        membershipIdTarget: UUID
    ) = transaction {
        findUnrelatedMembersByManyToOneMemberships(membershipIdsSources, membershipIdTarget)
            .apply {
                if (isNotEmpty())
                    addMembersToMembership(
                        memberIds = this,
                        membershipId = membershipIdTarget,
                        roleId = Role.Student.id
                    )
            }
    }

    /**
     * Find members who are exist in membership source but not exist in one of the membership targets.
     *
     * Skips members who are in the sources membership and in one of the membership targets
     *
     * Usage example: find course members enrolled by group who already removed in that group
     * @param membershipIdSource membership source where all desired members
     * @param membershipIdsTargets membership targets where can be unrelated members
     * @return user ids who exist membership in source but not exist in one of the membership targets
     */
    fun findUnrelatedMembersByOneToManyMemberships(
        membershipIdSource: UUID,
        membershipIdsTargets: List<UUID>
    ) = transaction {
        val sourceUm = UsersMemberships.alias("sourceUm")
        val targetUm = UsersMemberships.alias("targetUm")
        sourceUm.leftJoin(
            otherTable = targetUm,
            onColumn = { sourceUm[UsersMemberships.memberId] },
            otherColumn = { targetUm[UsersMemberships.memberId] },
            additionalConstraint = { targetUm[UsersMemberships.membershipId] inList membershipIdsTargets }
        )
            .slice(sourceUm[UsersMemberships.memberId])
            .select(
                targetUm[UsersMemberships.membershipId].isNull()
                        and (sourceUm[UsersMemberships.membershipId] eq membershipIdSource)
            ).map { it[sourceUm[UsersMemberships.memberId]].value }
    }

    /**
     * Find and remove members from source membership who are exist in membership source but not exist in one of the membership targets.
     *
     * Finding members using [findUnrelatedMembersByOneToManyMemberships]
     *
     * Usage example: find course members enrolled by group who already removed in that group and remove them from a course
     * @param membershipIdSource membership source where all desired members
     * @param membershipIdsTargets membership targets where can be unrelated members
     */
    fun findAndRemoveUnrelatedMembersByOneToManyMemberships(
        membershipIdSource: UUID,
        membershipIdsTargets: List<UUID>
    ) = transaction {
        findUnrelatedMembersByOneToManyMemberships(membershipIdSource, membershipIdsTargets)
            .apply {
                if (isNotEmpty())
                    removeMembersFromMembership(memberIds = this, membershipId = membershipIdSource)
            }
    }

    fun addMembersToMembership(memberIds: List<UUID>, membershipId: UUID, roleId: Long) = transaction {
        UsersMemberships.batchInsert(memberIds, ignore = true) {
            this[UsersMemberships.memberId] = it
            this[UsersMemberships.membershipId] = membershipId
        }
        addRoleMembersInScope(memberIds, roleId, membershipId)
    }

    private fun addRoleMembersInScope(
        memberIds: List<UUID>,
        roleId: Long,
        membershipId: UUID
    ) = UsersRolesScopes.batchInsert(memberIds, ignore = true) {
        this[UsersRolesScopes.userId] = it
        this[UsersRolesScopes.roleId] = roleId
        this[UsersRolesScopes.scopeId] = MembershipDao.findById(membershipId)!!
            .load(MembershipDao::scope).scopeId
    }

    fun removeMembersFromMembership(memberIds: List<UUID>, membershipId: UUID) = transaction {
        UsersMemberships.deleteWhere {
            memberId inList memberIds and (UsersMemberships.membershipId eq membershipId)
        }
        removeUsersRolesWhoNotExistInAnyMembershipByScopeId(memberIds, MembershipDao.findById(membershipId)!!.scopeId)
    }

    private fun getMembershipsByScope(scopeId: UUID): Query {
        return Memberships.select(Memberships.scopeId eq scopeId)
    }

    fun observeAddedMembersByMembershipId(membershipId: UUID): Flow<UUID> {
        logger.info { "observing added members by membership = $membershipId" }
        return insertUserMembershipFlow.mapNotNull {
            if (membershipId != it.record.getValue("membership_id").jsonPrimitive.content.toUUID())
                return@mapNotNull null
            it.record.getValue("member_id").jsonPrimitive.content.toUUID()
        }
    }

    fun observeRemovedMembersByMembershipId(membershipId: UUID): Flow<UUID> {
        return deleteUserMembershipFlow.mapNotNull {
            if (membershipId != it.oldRecord.getValue("membership_id").jsonPrimitive.content.toUUID())
                return@mapNotNull null
            it.oldRecord.getValue("member_id").jsonPrimitive.content.toUUID()
        }
    }

    private fun getMembersByMembershipAndMaxJoinTimestamp(membershipRow: ResultRow): Instant? {
        return UsersMemberships
            .slice(UsersMemberships.id, UsersMemberships.membershipId, UsersMemberships.joinAt.max())
            .select(UsersMemberships.membershipId eq membershipRow[Memberships.id])
            .singleOrNull()?.get(UsersMemberships.joinAt)
    }

    fun existMember(memberId: UUID, membershipId: UUID) = transaction {
        UsersMemberships.exists { UsersMemberships.memberId eq memberId and (UsersMemberships.membershipId eq membershipId) }
    }

    fun existMemberByScopeIds(memberId: UUID, scopeIds: List<UUID>) = transaction {
        UsersMemberships.innerJoin(
            otherTable = Memberships,
            onColumn = { Memberships.id },
            otherColumn = { UsersMemberships.membershipId }
        ).slice(UsersMemberships.memberId)
            .select(
                Memberships.scopeId inList scopeIds and (UsersMemberships.memberId eq memberId)
            ).count() > 0
    }

    fun existMemberByScopeIdAndRole(memberId: UUID, scopeId: UUID, roleId: Long) = transaction {
        UsersMemberships.innerJoin(
            otherTable = Memberships,
            onColumn = { Memberships.id },
            otherColumn = { UsersMemberships.membershipId }
        ).innerJoin(
            otherTable = UsersRolesScopes,
            onColumn = { Memberships.scopeId },
            otherColumn = { UsersRolesScopes.scopeId },
            additionalConstraint = { UsersRolesScopes.userId eq UsersMemberships.memberId })
            .slice(UsersMemberships.memberId)
            .select(
                Memberships.scopeId eq scopeId and (UsersMemberships.memberId eq memberId)
                        and (UsersRolesScopes.roleId eq roleId)
            ).count() > 0
    }

    fun existMemberByOneOfScopeIds(memberId: UUID, scopeIds: List<UUID>) = transaction {
        UsersMemberships.innerJoin(
            otherTable = Memberships,
            onColumn = { Memberships.id },
            otherColumn = { UsersMemberships.membershipId }
        ).slice(UsersMemberships.memberId)
            .select(
                Memberships.scopeId inList scopeIds and (UsersMemberships.memberId eq memberId)
            ).count() > 0
    }

    fun findMembershipIdsByMemberIdAndScopeId(userId: UUID, scopeId: UUID): List<UUID> {
        return Join(
            table = UsersMemberships,
            otherTable = Memberships,
            joinType = JoinType.INNER,
            onColumn = UsersMemberships.membershipId,
            otherColumn = Memberships.id
        ).slice(UsersMemberships.membershipId)
            .select(UsersMemberships.memberId eq userId and (Memberships.scopeId eq scopeId))
            .map { it[UsersMemberships.membershipId].value }
    }

    fun findMemberByMembershipTypesAndScopeId(
        userId: UUID,
        membershipTypes: List<String>,
        scopeId: UUID
    ): List<MembershipResponse> = Join(
        table = UsersMemberships,
        otherTable = Memberships,
        joinType = JoinType.INNER,
        onColumn = UsersMemberships.membershipId,
        otherColumn = Memberships.id
    ).slice(Memberships.id, Memberships.type).select(
        UsersMemberships.memberId eq userId and
                (Memberships.scopeId eq scopeId) and
                (Memberships.type inList membershipTypes)
    ).map { MembershipResponse(it[Memberships.id].value, it[Memberships.type]) }

    fun removeMemberByScopeId(userId: UUID, scopeId: UUID) {
        UsersMemberships.deleteWhere {
            memberId eq userId and (membershipId inList (
                    Memberships.slice(Memberships.id)
                        .select(Memberships.scopeId eq scopeId)
                        .map { it[Memberships.id] }
                    ))
        }
        UsersRolesScopes.deleteWhere {
            UsersRolesScopes.userId eq userId and (UsersRolesScopes.scopeId eq scopeId)
        }
    }
}