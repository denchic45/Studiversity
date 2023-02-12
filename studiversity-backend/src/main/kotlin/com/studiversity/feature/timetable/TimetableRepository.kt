package com.studiversity.feature.timetable

import com.studiversity.database.table.*
import com.studiversity.util.toSqlSortOrder
import com.stuiversity.api.timetable.model.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.between
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*

class TimetableRepository {

    fun putPeriodsOfDay(studyGroupId: UUID, date: LocalDate, periods: List<PeriodRequest>) {
        removeByStudyGroupIdAndDate(studyGroupId, date)
        periods.forEach { period ->
            val periodDao = PeriodDao.new {
                this.date = date
                order = period.order
                roomId = period.roomId
                this.studyGroupId = studyGroupId
                type = period.type
            }
            val periodId = periodDao.id.value

            when (val details = period.details) {
                is LessonDetails -> LessonDao.new(periodId) {
                    courseId = details.courseId
                }

                is EventDetails -> EventDao.new(periodId) {
                    name = details.name
                    color = details.color
                    icon = details.icon
                }
            }

            period.memberIds.forEach {
                PeriodMemberDao.new {
                    this.period = periodDao
                    this.member = UserDao.findById(it)!!
                }
            }
        }
    }

    fun findByDateRange(
        startDate: LocalDate,
        endDate: LocalDate,
        studyGroupId: List<UUID>? = null,
        memberIds: List<UUID>? = null,
        courseIds: List<UUID>? = null,
        roomIds: List<UUID>? = null,
        sorting: List<SortingPeriods>? = null
    ): TimetableResponse {
        val query = getPeriodsQuery(
            startDate = startDate,
            endDate = endDate,
            studyGroupId = studyGroupId,
            courseIds = courseIds,
            memberIds = memberIds,
            roomIds = roomIds,
            sorting = sorting
        )

        return PeriodDao.wrapRows(query)
            .orderBy(Periods.date to SortOrder.ASC, Periods.order to SortOrder.ASC)
            .map(PeriodDao::toResponse)
            .groupBy { it.date.dayOfWeek }
            .withDefault { emptyList() }
            .let { map ->
                TimetableResponse(
                    monday = map.getValue(DayOfWeek.MONDAY),
                    tuesday = map.getValue(DayOfWeek.TUESDAY),
                    wednesday = map.getValue(DayOfWeek.WEDNESDAY),
                    thursday = map.getValue(DayOfWeek.THURSDAY),
                    friday = map.getValue(DayOfWeek.FRIDAY),
                    saturday = map.getValue(DayOfWeek.SATURDAY),
                )
            }
    }

    fun findByDate(
        date: LocalDate,
        studyGroupId: List<UUID>? = null,
        memberIds: List<UUID>? = null,
        courseIds: List<UUID>? = null,
        roomIds: List<UUID>? = null,
        sorting: List<SortingPeriods>? = null
    ): TimetableOfDayResponse {
        val query = getPeriodsQuery(
            startDate = date,
            endDate = null,
            studyGroupId = studyGroupId,
            courseIds = courseIds,
            memberIds = memberIds,
            roomIds = roomIds,
            sorting = sorting
        )
        return PeriodDao.wrapRows(query)
            .orderBy(Periods.date to SortOrder.ASC, Periods.order to SortOrder.ASC)
            .map(PeriodDao::toResponse)
            .let(::TimetableOfDayResponse)
    }

    private fun getPeriodsQuery(
        startDate: LocalDate,
        endDate: LocalDate?,
        studyGroupId: List<UUID>?,
        courseIds: List<UUID>?,
        memberIds: List<UUID>?,
        roomIds: List<UUID>?,
        sorting: List<SortingPeriods>?
    ): Query {
        val query = Periods.innerJoin(Lessons, { id }, { id })
            .innerJoin(PeriodsMembers, { Periods.id }, { periodId })
            .run {
                endDate?.let {
                    select(Periods.date.between(startDate, endDate))
                } ?: select(Periods.date eq startDate)
            }

        studyGroupId?.let { query.andWhere { Periods.studyGroupId inList it } }

        courseIds?.let { query.andWhere { Lessons.courseId inList courseIds } }

        memberIds?.let { query.andWhere { PeriodsMembers.memberId inList memberIds } }

        roomIds?.let { query.andWhere { Periods.roomId inList roomIds } }

        sorting?.forEach {
            query.orderBy(
                column = when (it) {
                    is SortingPeriods.Course -> {
                        query.adjustColumnSet { innerJoin(Courses, { Lessons.courseId }, { id }) }
                            .adjustSlice { slice(fields + Courses.name) }
                        Courses.name
                    }

                    is SortingPeriods.Member -> {
                        query.adjustColumnSet { innerJoin(Users, { PeriodsMembers.memberId }, { id }) }
                            .adjustSlice { slice(fields + Users.surname) }
                        Users.surname
                    }

                    is SortingPeriods.Order -> Periods.order
                    is SortingPeriods.Room -> {
                        query.adjustColumnSet { innerJoin(Rooms, { Periods.roomId }, { id }) }
                            .adjustSlice { slice(fields + Rooms.name) }
                        Rooms.name
                    }

                    is SortingPeriods.StudyGroup -> {
                        query.adjustColumnSet { innerJoin(StudyGroups, { Periods.studyGroupId }, { id }) }
                            .adjustSlice { slice(fields + StudyGroups.name) }
                        StudyGroups.name
                    }
                },
                order = it.order.toSqlSortOrder()
            )
        }
        return query
    }

    fun removeByStudyGroupIdAndDate(studyGroupId: UUID, date: LocalDate) {
        Periods.deleteWhere { Periods.studyGroupId eq studyGroupId and (Periods.date eq date) }
    }

    fun removeByStudyGroupIdAndDateRange(studyGroupId: UUID, startDate: LocalDate, endDate: LocalDate) {
        Periods.deleteWhere { Periods.studyGroupId eq studyGroupId and (date.between(startDate, endDate)) }
    }
}