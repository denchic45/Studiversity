package com.studiversity.feature.course.repository

import com.studiversity.database.exists
import com.studiversity.database.table.*
import com.studiversity.feature.course.toResponse
import com.studiversity.supabase.deleteRecursive
import com.stuiversity.api.course.model.CourseResponse
import com.stuiversity.api.course.model.CreateCourseRequest
import com.stuiversity.api.course.model.UpdateCourseRequest
import io.github.jan.supabase.storage.BucketApi
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class CourseRepository(private val bucket: BucketApi) {

    fun add(request: CreateCourseRequest): CourseResponse {
        val dao = CourseDao.new {
            name = request.name
            request.subjectId?.apply {
                subject = SubjectDao.findById(this)
            }
        }
        return dao.toResponse()
    }

    fun findById(id: UUID): CourseResponse? {
        return CourseDao.findById(id)?.toResponse()
    }

    fun update(id: UUID, request: UpdateCourseRequest) = transaction {
        CourseDao.findById(id)?.apply {
            request.name.ifPresent { name = it }
            request.subjectId.ifPresent { subject = it?.let { SubjectDao.findById(it) } }
        }?.toResponse()
    }

    fun exist(id: UUID): Boolean {
        return Courses.exists { Courses.id eq id }
    }

    fun existStudyGroupByCourse(courseId: UUID, studyGroupId: UUID): Boolean {
        return ExternalStudyGroupsMemberships
            .innerJoin(Memberships, { membershipId }, { Memberships.id })
            .slice(ExternalStudyGroupsMemberships.studyGroupId)
            .exists { Memberships.scopeId eq courseId and (ExternalStudyGroupsMemberships.studyGroupId eq studyGroupId) }
    }

    fun findStudyGroupsByCourse(courseId: UUID): List<UUID> {
        return ExternalStudyGroupsMemberships
            .innerJoin(Memberships, { membershipId }, { Memberships.id })
            .slice(ExternalStudyGroupsMemberships.studyGroupId)
            .select(Memberships.scopeId eq courseId)
            .map { it[ExternalStudyGroupsMemberships.studyGroupId].value }
    }

    fun addCourseStudyGroup(courseId: UUID, studyGroupId: UUID) {
        val membershipId = Memberships
            .select(Memberships.scopeId eq courseId and (Memberships.type eq "by_group"))
            .firstOrNull()?.get(Memberships.id)
            ?: Memberships.insertIgnore {
                it[scopeId] = courseId
                it[active] = true
                it[type] = "by_group"
            }[Memberships.id]
        ExternalStudyGroupsMemberships.insertIgnore {
            it[ExternalStudyGroupsMemberships.membershipId] = membershipId
            it[ExternalStudyGroupsMemberships.studyGroupId] = studyGroupId
        }
    }

    fun removeCourseStudyGroup(courseId: UUID, studyGroupId: UUID): Boolean {
        val membershipByGroupId = Memberships.slice(Memberships.id).select(
            Memberships.scopeId eq courseId and
                    (Memberships.type eq "by_group")
        ).first()[Memberships.id]
        val courseStudyGroupDeleted = ExternalStudyGroupsMemberships.deleteWhere {
            ExternalStudyGroupsMemberships.studyGroupId eq studyGroupId and
                    (membershipId eq membershipByGroupId)
        } > 0
        if (!ExternalStudyGroupsMemberships.exists {
                ExternalStudyGroupsMemberships.membershipId eq membershipByGroupId
            })
            Memberships.deleteWhere { Memberships.id eq membershipByGroupId }
        return courseStudyGroupDeleted
    }

    fun addArchivedCourse(courseId: UUID) {
        CourseDao.findById(courseId)!!.archived = true
    }

    fun removeArchivedCourse(courseId: UUID) {
        CourseDao.findById(courseId)!!.archived = false
    }

    fun isArchivedCourse(courseId: UUID): Boolean {
        return CourseDao.findById(courseId)!!.archived
    }

    suspend fun removeCourse(courseId: UUID) {
        CourseDao.findById(courseId)!!.delete()
        bucket.deleteRecursive("courses/$courseId")
    }
}