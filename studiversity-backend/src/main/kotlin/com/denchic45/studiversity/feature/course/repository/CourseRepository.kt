package com.denchic45.studiversity.feature.course.repository

import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.course.model.CreateCourseRequest
import com.denchic45.stuiversity.api.course.model.UpdateCourseRequest
import com.denchic45.studiversity.database.exists
import com.denchic45.studiversity.database.table.*
import com.denchic45.studiversity.feature.course.toResponse
import com.denchic45.studiversity.supabase.deleteRecursive
import io.github.jan.supabase.storage.BucketApi
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.Instant
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

    fun find(
        memberId: UUID?,
        studyGroupId: UUID?,
        subjectId: UUID?,
        archived: Boolean?,
        q: String?
    ): List<CourseResponse> {
        val query = Courses.selectAll()

        if (memberId != null || studyGroupId != null) {
            query.adjustColumnSet {
                innerJoin(Memberships, { Courses.id }, { scopeId })
            }
        }

        memberId?.let {
            query
                .adjustColumnSet { innerJoin(UsersMemberships, { Memberships.id }, { membershipId }) }
                .andWhere { UsersMemberships.memberId eq memberId }
        }
        studyGroupId?.let {
            query.adjustColumnSet {
                innerJoin(ExternalStudyGroupsMemberships,
                    { Memberships.id },
                    { membershipId },
                    { Memberships.type eq "by_group" })
            }.andWhere { ExternalStudyGroupsMemberships.studyGroupId eq studyGroupId }
        }
        subjectId?.let {
            query.andWhere { Courses.subjectId eq subjectId }
        }
        archived?.let {
            query.andWhere { Courses.archived eq archived }
        }
        q?.let {
            query.adjustColumnSet { leftJoin(Subjects, { Courses.subjectId }, { Subjects.id }) }
                .adjustSlice { slice(fields + Subjects.id) }
                .andWhere {
                    Courses.name.lowerCase() like "%$q%" or
                            (Subjects.name.lowerCase() like "%$q%") or
                            (Subjects.shortname.lowerCase() like "%$q%")
                }
        }
        return CourseDao.wrapRows(query).map(CourseDao::toResponse)
    }

    fun update(id: UUID, request: UpdateCourseRequest): CourseResponse? = CourseDao.findById(id)?.apply {
        updatedAt = Instant.now()
        request.name.ifPresent { name = it }
        request.subjectId.ifPresent { subject = it?.let { SubjectDao.findById(it) } }
    }?.toResponse()


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

    fun removeStudyGroupFromCourse(courseId: UUID, studyGroupId: UUID): Boolean {
        val membershipOfCourseByGroupId = Memberships.slice(Memberships.id).select(
            Memberships.scopeId eq courseId and (Memberships.type eq "by_group")
        ).first()[Memberships.id]
        val courseStudyGroupDeleted = ExternalStudyGroupsMemberships.deleteWhere {
            ExternalStudyGroupsMemberships.studyGroupId eq studyGroupId and
                    (membershipId eq membershipOfCourseByGroupId)
        } > 0
        // Remove course membership for groups if no groups remain
        if (!ExternalStudyGroupsMemberships.exists {
                ExternalStudyGroupsMemberships.membershipId eq membershipOfCourseByGroupId
            })
            Memberships.deleteWhere { Memberships.id eq membershipOfCourseByGroupId }
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