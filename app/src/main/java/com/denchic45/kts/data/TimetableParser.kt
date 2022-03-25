package com.denchic45.kts.data

import android.util.Log
import com.denchic45.kts.data.model.domain.*
import com.denchic45.kts.data.model.domain.Event.Companion.createEmpty
import com.denchic45.kts.data.model.domain.SimpleEventDetails.Companion.dinner
import com.denchic45.kts.data.model.domain.SimpleEventDetails.Companion.practice
import com.denchic45.kts.utils.DatePatterns
import com.denchic45.kts.utils.Dates
import com.denchic45.kts.utils.toLocalDate
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFTable
import org.apache.poi.xwpf.usermodel.XWPFTableCell
import java.io.File
import java.time.LocalDate
import java.util.*
import java.util.stream.Collectors

class TimetableParser {
    private val days = listOf(
        "ПОНЕДЕЛЬНИК ",
        "ВТОРНИК",
        "СРЕДА",
        "ЧЕТВЕРГ",
        "ПЯТНИЦА",
        "СУББОТА"
    )
    private val specialSubjects: MutableList<Subject> = ArrayList()
    private var cellOfGroupPos = 0
    private lateinit var table: XWPFTable
    private var currentRow = 0
    private var currentDayOfWeek = 0
    private var currentGroupCourse: GroupCourses? = null

    suspend fun parseDoc(
        docFile: File,
        callbackGroupInfo: suspend (Int) -> List<GroupCourses>
    ): List<GroupTimetable> {

        val groupTimetableList: MutableList<GroupTimetable> = ArrayList()

        try {
            val wordDoc = XWPFDocument(docFile.inputStream())
            table = wordDoc.tables[0]

            val cellsInGroups: MutableList<XWPFTableCell> = ArrayList()
            cellsInGroups.addAll(table.getRow(1).tableCells)
            cellsInGroups.addAll(table.getRow(2).tableCells)

            val groupCoursesList = callbackGroupInfo(findCourseNumber())

            val cellsCount = table.getRow(1).tableCells.size

            for (groupCourses in groupCoursesList) {
                currentGroupCourse = groupCourses
                cellOfGroupPos = cellsInGroups.indexOf(
                    getCellByGroupName(
                        cellsInGroups,
                        groupCourses.group.name
                    )
                )
                if (cellOfGroupPos == -1) continue
                cellOfGroupPos =
                    if (cellOfGroupPos > cellsCount) cellOfGroupPos - cellsCount else cellOfGroupPos
                groupTimetableList.add(GroupTimetable(groupCourses.group, lessonsOfGroup))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Произошла проблема при извлечении документа")
        }


        Log.d("lol", "return timetables: ${groupTimetableList.size}")
        return groupTimetableList
    }

    private fun findCourseNumber(): Int {
        val text = table.getRow(0).getCell(0).text
        return text.replace(Regex("[^0-9]+"), "").toInt()
    }

    private fun getCellByGroupName(
        tableCells: List<XWPFTableCell>,
        groupName: String
    ): XWPFTableCell? {
        return tableCells
            .find { cell: XWPFTableCell ->
                val text = cell.text
                text.resetFormatting().contains(groupName.resetFormatting())
            }
    }

    private val lessonsOfGroup: List<EventsOfDay>
        get() {
            val weekLessons: MutableList<EventsOfDay> = ArrayList()
            currentRow = 3
            currentDayOfWeek = 0
            while (!table.getRow(currentRow).getCell(0).text.contains("ПОНЕДЕЛЬНИК")) currentRow++
            for (i in 0 until STUDY_DAY_COUNT) {
                val dateInCell = table.getRow(currentRow).getCell(0).text
                currentRow++
                val date = dateInCell.substring(dateInCell.lastIndexOf(" ") + 1)
                if (Dates.isValidDate(date, DatePatterns.DD_MM_yy)) {
                    weekLessons.add(getEventsOfTheDay(date))
                    currentDayOfWeek++
                } else {
                    throw  TimetableInvalidDateException("Некоректная дата: $dateInCell")
                }
            }
            return weekLessons
        }

    private fun getEventsOfTheDay(dateText: String): EventsOfDay {
        val date = dateText.toLocalDate(DatePatterns.DD_MM_yy)
        val events: MutableList<Event> = mutableListOf()
        if (currentRow == table.rows.size) return EventsOfDay.createEmpty(date)
        var cells = table.getRow(currentRow).tableCells
        while (hasRowsOfCurrentDate()) {
            val orderText = cells[0].text
            val subjectAndRoomText = cells[cellOfGroupPos].text
                .replace("\\(.*\\)".toRegex(), "")
                .trim { it <= ' ' }
            currentRow++
            if (currentRow < table.rows.size) {
                cells = table.getRow(currentRow).tableCells
            }
            if (orderText.isNotEmpty()) {
                events.add(createEvent(orderText.toInt(), subjectAndRoomText, date))
            } else if (subjectAndRoomText.isNotEmpty()) {
                throw TimetableOrderLessonException(
                    """
    У урока нет порядкового номера! 
    Дата: $dateText
    Поле урока: $subjectAndRoomText
    Группа: ${currentGroupCourse!!.group.name}
    """.trimIndent()
                )
            }
        }
        return EventsOfDay(date, events)
    }

    private fun hasRowsOfCurrentDate(): Boolean {
        if (currentRow == table.rows.size - 1) return false
        return if (currentDayOfWeek == 5) true
        else !table.getRow(currentRow)
            .getCell(0).text.contains(
                days[currentDayOfWeek + 1]
            )
    }

    private fun createEvent(order: Int, subjectAndRoom: String, date: LocalDate): Event {
        var subjectAndRoom = subjectAndRoom
        if (subjectAndRoom.isEmpty()) {
            return createEmpty(group = currentGroupCourse!!.group, order = order, date = date)
        }
        val separatorSubjectRoomPos = subjectAndRoom.indexOf('\\')
        val eventName: String
        val room: String
        subjectAndRoom = subjectAndRoom.replace("\\(.*\\)".toRegex(), "")
        if (separatorSubjectRoomPos != -1) {
            eventName = subjectAndRoom.substring(0, separatorSubjectRoomPos)
            room = subjectAndRoom.substring(separatorSubjectRoomPos + 1)
        } else {
            eventName = subjectAndRoom
            room = ""
        }
        val subject = findSubjectByName(eventName)
        return if (subject != null) {
            Event(
                group = currentGroupCourse!!.group,
                date = date,
                room = room,
                details = Lesson(subject, teachers = findTeachersBySubject(subject))
            )
        } else {
            Event(
                group = currentGroupCourse!!.group,
                date = date,
                room = room,
                details = createEventDetails(eventName)
            )
        }
    }

    private fun findSubjectByName(subjectName: String): Subject? {
        return currentGroupCourse!!.courses
            .firstOrNull { course ->
                course.subject.name.resetFormatting().contains(
                    subjectName.resetFormatting()
                )
            }?.subject
    }

    private fun createEventDetails(eventName: String): EventDetails {
        return when (eventName.lowercase(Locale.getDefault())) {
            "обед" -> dinner()
            "практика" -> practice()
            else -> SimpleEventDetails(eventName)
        }
    }

    private fun findTeachersBySubject(subject: Subject): List<User> {
        return currentGroupCourse!!.courses.stream()
            .filter { it.subject == subject }
            .map { it.teacher }
            .collect(Collectors.toList())
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