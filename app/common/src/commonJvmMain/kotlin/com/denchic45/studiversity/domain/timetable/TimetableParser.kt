package com.denchic45.studiversity.domain.timetable

import com.denchic45.studiversity.domain.timetable.model.TimetableParserResult
import com.denchic45.stuiversity.api.course.CoursesApi
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.course.subject.SubjectApi
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.denchic45.stuiversity.api.studygroup.StudyGroupApi
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.timetable.model.*
import com.denchic45.stuiversity.api.user.UserApi
import com.denchic45.stuiversity.api.user.model.UserResponse
import com.denchic45.stuiversity.util.DateTimePatterns
import com.denchic45.stuiversity.util.Dates
import com.denchic45.stuiversity.util.toLocalDate
import com.denchic45.stuiversity.util.toString
import com.github.michaelbull.result.unwrap
import me.tatarka.inject.annotations.Inject
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFTable
import org.apache.poi.xwpf.usermodel.XWPFTableCell
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.random.Random

@Inject
class TimetableParser(
    private val studyGroupApi: StudyGroupApi,
    private val userApi: UserApi,
    private val courseApi: CoursesApi,
    private val subjectApi: SubjectApi,
) {
    private val days = listOf(
        "ПОНЕДЕЛЬНИК ",
        "ВТОРНИК",
        "СРЕДА",
        "ЧЕТВЕРГ",
        "ПЯТНИЦА",
        "СУББОТА"
    )
    private var groupRow = 0
    private lateinit var table: XWPFTable
    private var currentRow = 0
    private var studyGroupColumn = 1
    private var currentDayOfWeek = 0
    private lateinit var currentStudyGroup: StudyGroupResponse

    private val cachedCourses = mutableMapOf<String, CourseResponse>()
    private val cachedCoursesByStudyGroup = mutableMapOf<Pair<String, UUID>, CourseResponse>()
    private val cachedSubjects = mutableListOf<SubjectResponse>()
    private val cachedEventsOfMissedSubjects = mutableSetOf<String>()
    private val cachedUsers = mutableMapOf<String, UserResponse>()

    private suspend fun findUserBySurname(surname: String): UserResponse? {
        return cachedUsers[surname] ?: userApi.getList(surname).unwrap().firstOrNull()
            ?.apply { cachedUsers[surname] = this }
    }

    suspend fun parseDoc(inputStream: InputStream): TimetableParserResult {
        return try {
            val wordDoc = XWPFDocument(inputStream)
            table = wordDoc.tables[0]

            val studyGroupsExtraRow = table.getRow(2).tableCells.size > 1

            val mondayRow = if (studyGroupsExtraRow) 3 else 2

            val monday = table.getRow(mondayRow).getCell(0)
                .text.split(" ")[1]
                .toLocalDate(DateTimePatterns.DD_MM_yy)

            val groupsCells: MutableList<XWPFTableCell> = ArrayList()
            groupsCells.addAll(table.getRow(1).tableCells.filter { it.text.isNotEmpty() })
            if (studyGroupsExtraRow) groupsCells.addAll(table.getRow(2).tableCells.filter { it.text.isNotEmpty() })

            val groupsCount = groupsCells.size
            val timetables: MutableList<Pair<StudyGroupResponse, TimetableResponse>> =
                mutableListOf()
            for (i in 0 until groupsCount) {
                val studyGroupName = groupsCells[i].text
                studyGroupApi.getList(query = studyGroupName)
                    .unwrap().firstOrNull { it.name == studyGroupName }?.let { studyGroup ->
                        currentStudyGroup = studyGroup
                        groupRow = getCellByGroupName(groupsCells, currentStudyGroup.name)?.tableRow
                            ?.let {
                                val indexOfRow = table.rows.indexOf(it)
                                if (studyGroupsExtraRow) indexOfRow + 1
                                else indexOfRow
                            } ?: -1

                        if (groupRow == -1) return@let
                        groupRow = if (groupRow > groupsCount)
                            groupRow - groupsCount
                        else groupRow
                        studyGroupColumn = i + 1
                        currentRow = if (studyGroupsExtraRow) 3 else 2
                        timetables.add(currentStudyGroup to createTimetable())
                    }
            }
            TimetableParserResult(monday.toString(DateTimePatterns.YYYY_ww), timetables)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    private fun getCellByGroupName(
        tableCells: List<XWPFTableCell>,
        groupName: String,
    ): XWPFTableCell? {
        return tableCells.find { cell: XWPFTableCell ->
            val text = cell.text
            text.resetFormatting().contains(groupName.resetFormatting())
        }
    }

    private suspend fun createTimetable(): TimetableResponse {
        val weekLessons = mutableListOf<List<PeriodResponse>>()
        currentDayOfWeek = 0
        val weekOfYear = table.getRow(currentRow)
            .getCell(0).text
            .split(" ")[1]
            .toLocalDate(DateTimePatterns.DD_MM_yy)
            .format(DateTimeFormatter.ofPattern("YYYY_ww"))

        for (i in 0 until STUDY_DAY_COUNT) {
            val dateInCell = table.getRow(currentRow).getCell(0).text
            currentRow++
            val date = dateInCell.substring(dateInCell.lastIndexOf(" ") + 1)
            if (Dates.isValidDate(date, DateTimePatterns.DD_MM_yy)) {
                weekLessons.add(getPeriodsOfDay(date))
                currentDayOfWeek++
            } else {
                throw TimetableInvalidDateException("Некоректная дата: $dateInCell")
            }
        }
        return TimetableResponse(
            weekOfYear,
            weekLessons[0],
            weekLessons[1],
            weekLessons[2],
            weekLessons[3],
            weekLessons[4],
            weekLessons[5],
        )
    }

    private suspend fun getPeriodsOfDay(dateText: String): List<PeriodResponse> {
        val date = dateText.toLocalDate(DateTimePatterns.DD_MM_yy)
        val periods = mutableListOf<PeriodResponse>()
        if (currentRow == table.rows.size) return emptyList()

        while (hasRowsOfCurrentDate()) {
            val cells = table.getRow(currentRow).tableCells
            val orderText = cells[0].text
            if (cells.size == 1) {
                currentRow++
                continue // Skip dinner
            }
            val cellContent: List<String> = cells[studyGroupColumn].paragraphs.map { it.paragraphText }
            val hasOrder = orderText.isNotEmpty()
            if (hasOrder) {
                if (orderText == "0" && cellContent.isEmpty()) {
                    continue
                }
                createPeriod(orderText.toInt(), date, cellContent)?.let { periods.add(it) }
            } else if (!cellContent.firstOrNull().isNullOrEmpty()) {
                throw TimetableOrderLessonException(
                    """
    У урока нет порядкового номера! 
    Дата: $dateText
    Поле урока: $cellContent
    Группа: ${currentStudyGroup.name}
    """.trimIndent()
                )
            }
            currentRow++
        }
        return periods
    }

    private fun hasRowsOfCurrentDate(): Boolean {
        if (currentRow == table.rows.size - 1) return false
        return if (currentDayOfWeek == 5) true
        else !table.getRow(currentRow).getCell(0).text.contains(days[currentDayOfWeek + 1])
    }

    private suspend fun createPeriod(
        order: Int,
        date: LocalDate,
        content: List<String>,
    ): PeriodResponse? {
        if (content.firstOrNull().isNullOrEmpty()) {
            return null
        }
        val subjectName = content[0].substringBefore('\\')
        val roomName = content[0].substringAfter('\\')

        val existEvent = findEventOfMissingSubjectByName(subjectName)
        val subject = if (existEvent) null else findSubjectByName(subjectName)
        val course = subject?.let { findCourseBySubjectAndStudyGroupId(it, currentStudyGroup.id) }
        return if (course != null) {
            LessonResponse(
                id = Random.nextLong(0, 1000),
                date = date,
                order = order,
                room = null,
                studyGroup = StudyGroupName(currentStudyGroup.id, currentStudyGroup.name),
                members = findTeacherByContent(content),
                details = LessonDetails(course)
            )
        } else {
            EventResponse(
                id = Random.nextLong(0, 1000),
                date = date,
                order = order,
                room = null,
                studyGroup = StudyGroupName(currentStudyGroup.id, currentStudyGroup.name),
                members = findTeacherByContent(content),
                // TODO: remove hardcoded url
                details = EventDetails(
                    subjectName,
                    "blue",
                    "https://twmjqqkhwizjfmbebbxj.supabase.co/storage/v1/object/public/main/icons/event.svg?t=2023-06-05T12%3A44%3A56.243Z"
                )
            ).apply { cachedEventsOfMissedSubjects.add(details.name) }
        }
    }

    private suspend fun findTeacherByContent(separatedContent: List<String>): List<PeriodMember> {
        return if (separatedContent.size == 1) {
            emptyList()
        } else separatedContent.subList(1, separatedContent.size).mapNotNull { line ->
            findUserBySurname(line.split(" ")[0])?.toPeriodMember()
        }
    }

    private suspend fun findCourseBySubjectAndStudyGroupId(
        subject: SubjectResponse,
        studyGroupId: UUID,
    ): CourseResponse? {
        val subjectName = subject.name

        // try to get cached course by group
        val cachedByGroup = cachedCoursesByStudyGroup[subjectName to studyGroupId]
        if (cachedByGroup != null) {
            return cachedByGroup
        }

        // try to get just cached course
        val cachedCourse = cachedCourses[subjectName]
        if (cachedCourse != null) {
            cachedCoursesByStudyGroup[subjectName to studyGroupId] = cachedCourse
            return cachedCourse
        }

        // get course by subject name and group id or subject name only
        val subjectResponse = subjectApi.getList(name = subjectName).unwrap().firstOrNull()
        val courseResponse = subjectResponse?.let {
            courseApi.getList(studyGroupId = studyGroupId, subjectId = subjectResponse.id)
                .unwrap().firstOrNull()
                ?: courseApi.getList(subjectId = subjectResponse.id).unwrap().firstOrNull()
        }

        return courseResponse?.also {
            cachedCourses[subjectName] = it
            cachedCoursesByStudyGroup[subjectName to studyGroupId] = it
        }
    }

    private fun findEventOfMissingSubjectByName(eventName: String): Boolean {
        return cachedEventsOfMissedSubjects.any { it.resetFormatting() == eventName.resetFormatting() }
    }

    private suspend fun findSubjectByName(subjectName: String): SubjectResponse? {
        return cachedSubjects.find { it.name.resetFormatting() == subjectName.resetFormatting() }
            ?: run {
                subjectApi.getList(name = subjectName).unwrap().firstOrNull()
            }?.also(cachedSubjects::add)
    }

    private fun String.resetFormatting(): String {
        return replace(" ", "")
            .replace("-", "")
            .replace("–", "")
            .lowercase(Locale.getDefault())
    }

    open class TimetableParserException(message: String?) : Exception(message)
    class TimetableInvalidDateException(message: String?) : TimetableParserException(message)
    class TimetableOrderLessonException(message: String?) : TimetableParserException(message)

    companion object {
        const val STUDY_DAY_COUNT = 6
    }
}