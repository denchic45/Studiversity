package com.studiversity.feature.studygroup.repository

import com.studiversity.database.exists
import com.studiversity.database.table.Specialties
import com.studiversity.database.table.SpecialtyDao
import com.studiversity.database.table.StudyGroupDao
import com.studiversity.database.table.StudyGroups
import com.studiversity.feature.studygroup.mapper.toResponse
import com.denchic45.stuiversity.util.toUUID
import com.denchic45.stuiversity.api.studygroup.model.CreateStudyGroupRequest
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.studygroup.model.UpdateStudyGroupRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import java.util.*

class StudyGroupRepository {

    fun add(request: CreateStudyGroupRequest): StudyGroupResponse {
        val dao = StudyGroupDao.new {
            name = request.name
            academicYear = listOf(request.academicYear.start, request.academicYear.end)
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
                    update[StudyGroups.academicYear.column] = arrayOf(it.start.toShort(), it.end.toShort())
                }
                specialtyId.ifPresent { update[StudyGroups.specialtyId] = it }
            }
        }.run { this != 0 }
    }

    fun findById(id: UUID): StudyGroupResponse? = StudyGroupDao.findById(id)?.toResponse()

    fun find(query: String) = StudyGroupDao.wrapRows(
        StudyGroups.leftJoin(Specialties, { specialtyId }, { Specialties.id })
            .select(
                StudyGroups.name.lowerCase().trim() like "%$query%"
                        or (Specialties.name.lowerCase().trim() like "%$query%")
                        or (Specialties.shortname.lowerCase().trim() like "%$query%")
            )
    ).map(StudyGroupDao::toResponse)

    fun remove(id: UUID) = StudyGroups.deleteWhere { StudyGroups.id eq id }.run { this != 0 }

    fun exist(id: UUID): Boolean {
        return StudyGroups.exists { StudyGroups.id eq id }
    }
}