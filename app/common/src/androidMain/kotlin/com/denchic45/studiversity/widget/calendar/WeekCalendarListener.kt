package com.denchic45.studiversity.widget.calendar

import java.time.LocalDate

interface WeekCalendarListener {
    fun onDaySelect(date: LocalDate)
    fun onWeekSelect(weekItem: com.denchic45.studiversity.widget.calendar.model.WeekItem)
    interface OnLoadListener {
        fun onWeekLoad(weekItem: com.denchic45.studiversity.widget.calendar.model.WeekItem)
    }
}