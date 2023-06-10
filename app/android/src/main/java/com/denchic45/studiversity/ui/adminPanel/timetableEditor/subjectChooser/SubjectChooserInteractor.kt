package com.denchic45.studiversity.ui.adminPanel.timetableEditor.subjectChooser

import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectChooserInteractor @Inject constructor() {

    private val selectedSubject = Channel<SubjectResponse>()

    suspend fun receive(): SubjectResponse {
        return selectedSubject.receive()
    }


    suspend fun postSelectedSubject(subject: SubjectResponse) {
        selectedSubject.send(subject)
    }
}