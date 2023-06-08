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
    private val onResult: (TimetableParserResult) -> Unit,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    val uiState = MutableStateFlow<UiState>(UiState.None)
    val showWeekPicker = MutableStateFlow(false)
    val showFilePicker = MutableStateFlow(false)

    fun onCreateEmpty() {
        showWeekPicker.value = true
    }

    fun onParseDoc() {
        showFilePicker.value = true
    }

    fun onWeekSelect(weekOfYear: String) {
        onResult(TimetableParserResult(weekOfYear, listOf()))
        showWeekPicker.value = false
    }

    fun onCancelWeekPicker() {
        showWeekPicker.value = false
    }

    fun onFileSelect(file: Path?) {
        showFilePicker.update { false }
        file?.let {
            componentScope.launch {
                try {
                    uiState.value = UiState.Loading("Извлечение расписания")
                    val result = parseTimetableUseCase(file)
                    uiState.value = UiState.None
                    withContext(Dispatchers.Main) {
                        onResult(result)
                    }
                } catch (throwable: Exception) {
                    throwable.printStackTrace()
                    uiState.value = UiState.Error(throwable.message ?: "")
                }
            }
        }
    }

    sealed interface UiState {
        object None : UiState

        data class Loading(val message: String) : UiState

        data class Error(val message: String) : UiState

    }

    fun onErrorClose() {
        uiState.value = UiState.None
    }
}