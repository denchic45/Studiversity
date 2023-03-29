package com.denchic45.kts.di.component

import com.denchic45.kts.ui.timetable.TimetableViewModel
import me.tatarka.inject.annotations.Component


@Component
abstract class ViewModelComponent {

    abstract val timetableViewModel: () -> TimetableViewModel
}