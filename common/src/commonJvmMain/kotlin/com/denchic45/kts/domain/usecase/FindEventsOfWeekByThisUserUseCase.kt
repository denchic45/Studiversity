package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.EventRepository
import com.denchic45.kts.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.tatarka.inject.annotations.Inject
import java.time.LocalDate

@Inject
class FindEventsOfWeekByThisUserUseCase(private val eventRepository: EventRepository) {

    operator fun invoke(): Flow<GroupTimetable> {
        val groupHeader = GroupHeader("1", "ПКС", "2")
        val physicalCultureEvent = {
            Event("",
                groupHeader,
                details = Lesson(Subject("",
                    "Физкультура",
                    "https://firebasestorage.googleapis.com/v0/b/kts-app-2ab1f.appspot.com/o/subjects%2Fbasketball.svg?alt=media&token=a6d05944-24e2-4cbd-940f-32e39e1ea4b3",
                    "blue"),
                    listOf()))
        }
        return flowOf(
            GroupTimetable(groupHeader,
                listOf(
                    EventsOfDay(LocalDate.parse("2022-09-05"),
                        mutableListOf(
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent()
                        ), id = "1"),
                    EventsOfDay(LocalDate.parse("2022-09-06"),
                        mutableListOf(
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent()
                        ), id = "1"),
                    EventsOfDay(LocalDate.parse("2022-09-07"),
                        mutableListOf(
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent()
                        ), id = "1"),
                    EventsOfDay(LocalDate.parse("2022-09-08"),
                        mutableListOf(
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent()
                        ), id = "1"),
                    EventsOfDay(LocalDate.parse("2022-09-09"),
                        mutableListOf(
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent()
                        ), id = "1"),
                    EventsOfDay(LocalDate.parse("2022-09-10"),
                        mutableListOf(
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent(),
                            physicalCultureEvent()
                        ), id = "1")
                ))
        )
    }
}