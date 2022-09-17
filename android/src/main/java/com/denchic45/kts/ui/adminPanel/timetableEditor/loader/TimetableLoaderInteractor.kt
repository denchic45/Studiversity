package com.denchic45.kts.ui.adminPanel.timetableEditor.loader

import com.denchic45.kts.domain.Interactor
import com.denchic45.kts.data.repository.EventRepository
import com.denchic45.kts.data.repository.GroupRepository
import com.denchic45.kts.data.repository.SubjectRepository
import com.denchic45.kts.domain.TimetableParser
import com.denchic45.kts.domain.model.GroupTimetable
import java.io.File
import javax.inject.Inject

class TimetableLoaderInteractor @Inject constructor(
    private val eventRepository: EventRepository,
    private val groupRepository: GroupRepository,
    private val subjectRepository: SubjectRepository,
) : Interactor {

    suspend fun parseDocumentTimetable(docFile: File): List<GroupTimetable> {
        return TimetableParser().parseDoc(docFile) { course: Int ->
            groupRepository.findGroupsWithCoursesByCourse(course)
        }
    }

    override fun removeListeners() {
        groupRepository.removeListeners()
        eventRepository.removeListeners()
        subjectRepository.removeListeners()
    }

    suspend fun addTimetables(groupWeekLessons: List<GroupTimetable>) {
        eventRepository.addGroupTimetables(groupWeekLessons)
    }
}