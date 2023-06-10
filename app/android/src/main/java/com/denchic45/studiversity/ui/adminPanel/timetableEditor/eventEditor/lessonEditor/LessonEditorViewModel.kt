package com.denchic45.studiversity.ui.adminPanel.timetableEditor.eventEditor.lessonEditor
//
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.viewModelScope
//import com.denchic45.studiversity.R
//import com.denchic45.studiversity.SingleLiveData
//import com.denchic45.studiversity.data.domain.model.EventType
//import com.denchic45.studiversity.domain.model.Lesson
//import com.denchic45.studiversity.domain.model.Subject
//import com.denchic45.studiversity.domain.model.User
//import com.denchic45.studiversity.ui.adminPanel.timetableEditor.eventEditor.EventEditorInteractor
//import com.denchic45.studiversity.ui.adminPanel.timetableEditor.subjectChooser.SubjectChooserInteractor
//import com.denchic45.studiversity.ui.base.BaseViewModel
//import com.denchic45.studiversity.ui.teacherChooser.TeacherChooserInteractor
//import com.denchic45.studiversity.uieditor.UIEditor
//import com.denchic45.studiversity.uivalidator.Rule
//import com.denchic45.studiversity.uivalidator.UIValidator
//import com.denchic45.studiversity.uivalidator.Validation
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//class LessonEditorViewModel @Inject constructor(
//    private val interactor: EventEditorInteractor
//) : BaseViewModel() {
//
//    val subjectField = MutableLiveData<Subject>()
//
//    val openChoiceOfTeacher = SingleLiveData<Void>()
//
//    val teachersField = MutableLiveData<MutableList<User>>()
//
//    val showErrorField = MutableLiveData<Pair<Int, Boolean>>()
//
//    val openChoiceOfGroupSubject = SingleLiveData<Void>()
//    private val uiEditor: UIEditor<Lesson> = UIEditor(interactor.isNew) {
//        Lesson(
//            subjectField.value ?: Subject.createEmpty(),
//            teachersField.value ?: emptyList()
//        )
//    }
//
//    @Inject
//    lateinit var subjectChooserInteractor: SubjectChooserInteractor
//
//    @Inject
//    lateinit var teacherChooserInteractor: TeacherChooserInteractor
//
//    private fun fillFields() {
//        if (interactor.oldEvent.value!!.details is Lesson && interactor.oldEvent.value!!.details != Lesson.createEmpty()) {
//            (interactor.oldEvent.value!!.details as Lesson).apply {
//                subjectField.value = subject
//                teachersField.value = teachers.toMutableList()
//            }
//        }
//    }
//
//    fun onSubjectClick() {
//        viewModelScope.launch {
//            subjectChooserInteractor.groupName = interactor.oldEvent.value!!.groupHeader.name
//            subjectChooserInteractor.groupId = interactor.oldEvent.value!!.groupHeader.id
//            openChoiceOfGroupSubject.call()
//            subjectChooserInteractor.receive().let { subject: Subject ->
//                subjectField.value = subject
//            }
//        }
//    }
//
//    fun onAddTeacherItemClick() {
//        viewModelScope.launch {
//            openChoiceOfTeacher.call()
//            teacherChooserInteractor.receiveSelectTeacher().apply {
//                teachersField.value =
//                    teachersField.value?.let { it.add(this); it } ?: mutableListOf(this)
//            }
//        }
//    }
//
//    fun onRemoveTeacherItemClick(position: Int) {
//        teachersField.value = uiEditor.item.teachers.toMutableList().apply { removeAt(position) }
//    }
//
//    init {
//        interactor.getDetails = { uiEditor.item }
//
//        with(interactor.oldEvent.value!!.details) {
//            if (this.eventType == EventType.LESSON) {
//                uiEditor.oldItem = interactor.oldEvent.value!!.details as Lesson
//            }
//        }
//
//
//        val uiValidator = UIValidator.of(
//            Validation(Rule { subjectField.value != null })
//                .sendActionResult(
//                    { showErrorField.setValue(Pair(R.id.rl_subject, true)) }
//                ) { showErrorField.setValue(Pair(R.id.rl_subject, false)) },
//            Validation(
//                Rule({ !teachersField.value.isNullOrEmpty() }, "Нет преподавателя")
//            ).sendMessageResult(toast)
//        )
//        interactor.validateEventDetails = { uiValidator.runValidates() }
//        fillFields()
//    }
//}