package com.studiversity.feature.course.subject

import com.studiversity.database.table.CourseDao
import com.studiversity.database.table.Courses
import com.studiversity.database.table.SubjectDao
import com.studiversity.database.table.Subjects
import com.stuiversity.api.course.subject.model.CreateSubjectRequest
import com.stuiversity.api.course.subject.model.UpdateSubjectRequest
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.trim
import java.util.*

class SubjectRepository {
    fun add(request: CreateSubjectRequest) = transaction {
        SubjectDao.new {
            name = request.name
            shortname = request.shortname
            iconName = request.iconName
        }.toResponse()
    }

    fun findById(id: UUID) = transaction {
        SubjectDao.findById(id)?.toResponse()
    }

    fun findAll() = transaction {
        SubjectDao.all().toResponses()
    }

    fun find(query: String) = SubjectDao.find(
        Subjects.name.lowerCase().trim() like "%$query%"
                or (Subjects.shortname.lowerCase().trim() like "%$query%")
    ).map(SubjectDao::toResponse)

    fun update(id: UUID, request: UpdateSubjectRequest) = transaction {
        SubjectDao.findById(id)?.apply {
            request.name.ifPresent { name = it }
            request.shortname.ifPresent { name = it }
            request.iconName.ifPresent { iconName = it }
        }.run { this?.toResponse() }
    }

    fun remove(id: UUID) = transaction {
        val subjectDao = SubjectDao.findById(id) ?: return@transaction null
        CourseDao.find(Courses.subjectId eq id).forEach {
            it.subjectId = null
        }
        subjectDao.delete()
    }
}