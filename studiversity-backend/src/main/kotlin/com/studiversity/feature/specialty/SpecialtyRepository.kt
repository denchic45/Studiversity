package com.studiversity.feature.specialty

import com.studiversity.database.table.SpecialtyDao
import com.stuiversity.api.specialty.model.CreateSpecialtyRequest
import com.stuiversity.api.specialty.model.SpecialtyResponse
import com.stuiversity.api.specialty.model.UpdateSpecialtyRequest
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
}