package com.studiversity.feature.studygroup.repository

import com.denchic45.stuiversity.api.studygroup.model.CreateStudyGroupRequest
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.studygroup.model.UpdateStudyGroupRequest
import com.studiversity.database.exists
import com.studiversity.database.table.*
import com.studiversity.feature.studygroup.mapper.toResponse
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.Instant
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

    fun update(id: UUID, updateStudyGroupRequest: UpdateStudyGroupRequest): StudyGroupResponse? {
        return StudyGroupDao.findById(id)?.also { dao ->
            updateStudyGroupRequest.apply {
                name.ifPresent { dao.name = it }
                academicYear.ifPresent {
                    dao.startAcademicYear = it.start
                    dao.endAcademicYear = it.end
                }
                specialtyId.ifPresent { dao.specialty = it?.let { SpecialtyDao.findById(it) } }
            }
            dao.updatedAt = Instant.now()
        }?.toResponse()
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
            .leftJoin(
                MembershipsInnerUserMembershipsInnerUsersRolesScopes,
                { StudyGroups.id },
                { Memberships.scopeId })
            .selectAll()
        q?.let {
            query.andWhere {
                StudyGroups.name.lowerCase().trim() like "%$q%" or
                        (Specialties.name.lowerCase().trim() like "%$q") or
                        (Specialties.shortname.lowerCase().trim() like "%$q")
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