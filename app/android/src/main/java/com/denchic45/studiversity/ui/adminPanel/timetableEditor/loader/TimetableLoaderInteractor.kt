//package com.denchic45.studiversity.ui.adminPanel.timetableEditor.loader
//
//import com.denchic45.studiversity.domain.Interactor
//import com.denchic45.studiversity.data.repository.EventRepository
//import com.denchic45.studiversity.data.repository.StudyGroupRepository
//import com.denchic45.studiversity.data.repository.SubjectRepository
//import com.denchic45.studiversity.domain.timetable.TimetableParser
//import com.denchic45.studiversity.domain.model.GroupTimetable
//import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
//import com.denchic45.stuiversity.api.timetable.model.PutTimetableRequest
//import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
//import java.io.File
//import java.util.*
//import javax.inject.Inject
//
//class TimetableLoaderInteractor @Inject constructor(
//    private val eventRepository: EventRepository,
//    private val studyGroupRepository: StudyGroupRepository,
//    private val subjectRepository: SubjectRepository,
//    private val timetableParser:()->TimetableParser
//) : Interactor {
//
//    suspend fun parseDocumentTimetable(docFile: File): List<Pair<StudyGroupResponse, TimetableResponse>> {
//        return timetableParser().parseDoc(docFile)
//    }
//
//    override fun removeListeners() {
//        studyGroupRepository.removeListeners()
//        eventRepository.removeListeners()
//        subjectRepository.removeListeners()
//    }
//
//    suspend fun addTimetables(weekOfYear:String, putTimetableRequests: List<PutTimetableRequest>) {
//        eventRepository.putTimetables(weekOfYear, putTimetableRequests)
//    }
//}