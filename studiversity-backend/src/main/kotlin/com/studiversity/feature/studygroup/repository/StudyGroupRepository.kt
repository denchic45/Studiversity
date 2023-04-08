package com.studiversity.feature.studygroup.repository

import com.denchic45.stuiversity.api.studygroup.model.CreateStudyGroupRequest
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.studygroup.model.UpdateStudyGroupRequest
import com.studiversity.database.exists
import com.studiversity.database.table.*
import com.studiversity.feature.studygroup.mapper.toResponse
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

class StudyGroupRepository {

    fun add(request: CreateStudyGroupRequest): StudyGroupResponse {
        val dao = StudyGroupDao.new {
            name = request.name
            startAcademicYear = request.academicYear.start
            endAcademicYear = request.academicYear.end
            request.specialtyId?.apply {
                specialty = SpecialtyDao.findById(this)
            }
        }
        return dao.toResponse()
    }

    fun update(id: UUID, updateStudyGroupRequest: UpdateStudyGroupRequest): Boolean {
        return StudyGroups.update({ StudyGroups.id eq id }) { update ->
            updateStudyGroupRequest.apply {
                name.ifPresent { update[StudyGroups.name] = it }
                academicYear.ifPresent {
                    update[startAcademicYear] = it.start
                    update[endAcademicYear] = it.end
                }
                specialtyId.ifPresent { update[StudyGroups.specialtyId] = it }
            }
        }.run { this != 0 }
    }

    fun findById(id: UUID): StudyGroupResponse? = StudyGroupDao.findById(id)?.toResponse()

    fun find(
        q: String?,
        memberId: UUID?,
        roleId: Long?,
        specialtyId: UUID?,
        academicYear: Int?
    ): List<StudyGroupResponse> {
        val query = StudyGroups.leftJoin(Specialties, { this.specialtyId }, { Specialties.id })
            .innerJoin(
                MembershipsInnerUserMembershipsInnerUsersRolesScopes,
                { StudyGroups.id },
                { Memberships.scopeId })
            .selectAll()
        q?.let {
            query.andWhere {
                StudyGroups.name.lowerCase().trim() like "%$query%" or
                        (Specialties.name.lowerCase().trim() like "%$query%") or
                        (Specialties.shortname.lowerCase().trim() like "%$query%")
            }
        }
        memberId?.let {
            query.andWhere { UsersMemberships.memberId eq memberId }
        }
        roleId?.let {
            query.andWhere { UsersRolesScopes.roleId eq it }
        }
        specialtyId?.let {
            query.andWhere { Specialties.id eq it }
        }
        academicYear?.let {
            query.andWhere {
                (StudyGroups.startAcademicYear - StudyGroups.endAcademicYear) eq it
            }
        }
        return StudyGroupDao.wrapRows(query).map(StudyGroupDao::toResponse)
    }

    fun remove(id: UUID) = StudyGroups.deleteWhere { StudyGroups.id eq id }.run { this != 0 }

    fun exist(id: UUID): Boolean {
        return StudyGroups.exists { StudyGroups.id eq id }
    }
}