package com.denchic45.studiversity.feature.course.subject

import com.denchic45.studiversity.database.table.CourseDao
import com.denchic45.studiversity.database.table.Courses
import com.denchic45.studiversity.database.table.SubjectDao
import com.denchic45.studiversity.database.table.Subjects
import com.denchic45.stuiversity.api.course.subject.model.CreateSubjectRequest
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.denchic45.stuiversity.api.course.subject.model.UpdateSubjectRequest
import io.github.jan.supabase.storage.BucketApi
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class SubjectRepository(private val bucket: BucketApi) {

    fun add(request: CreateSubjectRequest) = transaction {
        SubjectDao.new {
            name = request.name
            shortname = request.shortname
            iconUrl = request.iconUrl
        }.toResponse()
    }

    fun findById(id: UUID) = transaction {
        SubjectDao.findById(id)?.toResponse()
    }

    fun find(q: String?): List<SubjectResponse> {
        val query = Subjects.selectAll()
        q?.let {
            query.andWhere {
                Subjects.name.lowerCase().trim() like "%$q%" or
                        (Subjects.shortname.lowerCase().trim() like "%$q%")
            }
        }
        return SubjectDao.wrapRows(query).map(SubjectDao::toResponse)
    }

    fun update(id: UUID, request: UpdateSubjectRequest) = transaction {
        SubjectDao.findById(id)?.apply {
            request.name.ifPresent { name = it }
            request.shortname.ifPresent { shortname = it }
            request.iconUrl.ifPresent { iconUrl = it }
        }.run { this?.toResponse() }
    }

    fun remove(id: UUID) = transaction {
        val subjectDao = SubjectDao.findById(id) ?: return@transaction null
        CourseDao.find(Courses.subjectId eq id).forEach {
            it.subjectId = null
        }
        subjectDao.delete()
    }

    suspend fun findIconsUrls(): List<String> {
        return bucket.list("subjects_icons").map { bucket.publicUrl("subjects_icons/${it.name}") }
    }
}