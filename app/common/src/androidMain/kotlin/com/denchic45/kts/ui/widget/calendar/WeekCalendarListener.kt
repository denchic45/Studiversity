package com.denchic45.kts.ui.widget.calendar

import com.denchic45.kts.ui.widget.calendar.model.WeekItem
import java.time.LocalDate

interface WeekCalendarListener {
    fun onDaySelect(date: LocalDate)
    fun onWeekSelect(weekItem: WeekItem)
    interface OnLoadListener {
        fun onWeekLoad(weekItem: WeekItem)
    }
}