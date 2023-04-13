package com.denchic45.kts.ui.timetableLoader

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.usecase.ParseTimetableUseCase
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import okio.Path

@Inject
class TimetableCreatorComponent(
    private val parseTimetableUseCase: ParseTimetableUseCase,
    @Assisted
    private val onCreate: (String, List<Pair<StudyGroupResponse, TimetableResponse>>) -> Unit,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    val showWeekPicker = MutableStateFlow(false)
    val showFilePicker = MutableStateFlow(false)
    val showErrorDialog = MutableStateFlow<String?>(null)

    fun onCreateEmpty() {
        showWeekPicker.value = true
    }

    fun onLoadFromFile() {
        showFilePicker.value = true
    }

    fun onWeekSelect(weekOfYear: String) {
        onCreate(weekOfYear, listOf())
        showWeekPicker.value = false
    }

    fun onCancelWeekPicker() {
        showWeekPicker.value = false
    }

    fun onFileSelect(file: Path) {
        componentScope.launch {
            try {
                val timetable = parseTimetableUseCase(file)
                onCreate(timetable[0].second.weekOfYear, timetable)
            } catch (throwable: Exception) {
                throwable.printStackTrace()
                showErrorDialog.value = throwable.message
            }
        }
    }
}