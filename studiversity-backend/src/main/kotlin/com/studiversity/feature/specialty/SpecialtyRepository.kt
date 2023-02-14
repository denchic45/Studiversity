package com.studiversity.feature.specialty

import com.studiversity.database.table.Specialties
import com.studiversity.database.table.SpecialtyDao
import com.stuiversity.api.specialty.model.CreateSpecialtyRequest
import com.stuiversity.api.specialty.model.SpecialtyResponse
import com.stuiversity.api.specialty.model.UpdateSpecialtyRequest
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.or
import java.util.*

class SpecialtyRepository {

    fun add(createSpecialtyRequest: CreateSpecialtyRequest): SpecialtyResponse {
        return SpecialtyDao.new {
            name = createSpecialtyRequest.name
            shortname = createSpecialtyRequest.shortname
        }.toResponse()
    }

    fun update(id: UUID, updateSpecialtyRequest: UpdateSpecialtyRequest): SpecialtyResponse? {
        return SpecialtyDao.findById(id)?.apply {
            updateSpecialtyRequest.name.ifPresent {
                name = it
            }
            updateSpecialtyRequest.shortname.ifPresent {
                shortname = it
            }
        }?.toResponse()
    }

    fun findById(id: UUID): SpecialtyResponse? {
        return SpecialtyDao.findById(id)?.toResponse()
    }

    fun remove(id: UUID): Boolean {
        return SpecialtyDao.findById(id)?.delete() != null
    }

    fun find(query: String): List<SpecialtyResponse> =
        SpecialtyDao.find(
            Specialties.name.lowerCase() like "%$query%"
                    or (Specialties.shortname.lowerCase() like "%$query%")
        ).map(SpecialtyDao::toResponse)
}