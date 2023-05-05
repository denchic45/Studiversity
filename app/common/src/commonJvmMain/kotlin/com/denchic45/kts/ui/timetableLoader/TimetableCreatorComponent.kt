package com.denchic45.kts.ui.timetableLoader

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.timetable.model.TimetableParserResult
import com.denchic45.kts.domain.usecase.ParseTimetableUseCase
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import okio.Path

@Inject
class TimetableCreatorComponent(
    private val parseTimetableUseCase: ParseTimetableUseCase,
    @Assisted
    private val onCreate: (TimetableParserResult) -> Unit,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    val showWeekPicker = MutableStateFlow(false)
    val showFilePicker = MutableStateFlow(false)
    val errorMessage = MutableStateFlow<String?>(null)

    fun onCreateEmpty() {
        showWeekPicker.value = true
    }

    fun onLoadFromFile() {
        showFilePicker.value = true
    }

    fun onWeekSelect(weekOfYear: String) {
        onCreate(TimetableParserResult(weekOfYear, listOf()))
        showWeekPicker.value = false
    }

    fun onCancelWeekPicker() {
        showWeekPicker.value = false
    }

    fun onFileSelect(file: Path) {
        showFilePicker.update { false }
        componentScope.launch {
            try {
                val result = parseTimetableUseCase(file)
                withContext(Dispatchers.Main) {
                    onCreate(result)
                }
            } catch (throwable: Exception) {
                throwable.printStackTrace()
                errorMessage.emit(throwable.message ?: "")
            }
        }
    }

    fun onErrorClose() {
        errorMessage.value = null
    }
}