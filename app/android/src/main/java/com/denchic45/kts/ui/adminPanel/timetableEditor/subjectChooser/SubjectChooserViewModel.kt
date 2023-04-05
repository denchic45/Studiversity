package com.denchic45.kts.ui.adminPanel.timetableEditor.subjectChooser

import androidx.lifecycle.viewModelScope
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.usecase.FindSubjectByContainsNameUseCase
import com.denchic45.kts.ui.base.chooser.ChooserViewModel
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SubjectChooserViewModel @Inject constructor(
    private val subjectChooserInteractor: SubjectChooserInteractor,
    private val findSubjectByContainsNameUseCase: FindSubjectByContainsNameUseCase
) : ChooserViewModel<SubjectResponse>() {

    override val sourceFlow: (String) -> Flow<Resource<List<SubjectResponse>>> =
        { name: String -> flow { emit(findSubjectByContainsNameUseCase(name)) } }


    override fun onItemClick(position: Int) {
        items.value.onSuccess {
            viewModelScope.launch {
                subjectChooserInteractor.postSelectedSubject(it[position])
                finish()
            }
        }
    }
}