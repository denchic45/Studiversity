package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.EventRepository
import com.denchic45.kts.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.tatarka.inject.annotations.Inject
import java.time.LocalDate

@Inject
class FindEventsOfWeekByThisUserUseCase(private val eventRepository: EventRepository) {

    operator fun invoke(monday: LocalDate): Flow<List<EventsOfDay>> {
        return eventRepository.findTimetableByYourGroupAndWeek(monday)
    }

    private fun testData(): Flow<List<EventsOfDay>> {
        val groupHeader = GroupHeader("1", "ПКС", "2")
        val physicalCultureEvent = Event("",
            groupHeader,
            details = Lesson(Subject("",
                "Физкультура",
                "ic_basketball",
                "blue"),
                listOf()))

        val emptyEvent = Event("",
            groupHeader,
            details = EmptyEventDetails())

        return flowOf(
            listOf(
                EventsOfDay(LocalDate.parse("2022-09-05"),
                    listOf(
                        physicalCultureEvent,
                        physicalCultureEvent,
                        physicalCultureEvent,
                        physicalCultureEvent,
                        physicalCultureEvent,
                        physicalCultureEvent,
                        physicalCultureEvent
                    ), id = "1"),
                EventsOfDay(LocalDate.parse("2022-09-06"),
                    listOf(
                        physicalCultureEvent,
                        physicalCultureEvent,
                        emptyEvent,
                        emptyEvent,
                        physicalCultureEvent,
                        physicalCultureEvent,
                        physicalCultureEvent
                    ), id = "1"),
                EventsOfDay(LocalDate.parse("2022-09-07"),
                    listOf(
                        physicalCultureEvent,
                        physicalCultureEvent,
                        physicalCultureEvent,
                        physicalCultureEvent,
                        physicalCultureEvent,
                        physicalCultureEvent,
                        physicalCultureEvent
                    ),
                    startsAtZero = true,
                    id = "1"),
                EventsOfDay(LocalDate.parse("2022-09-08"),
                    listOf(
                        physicalCultureEvent,
                        physicalCultureEvent,
                        physicalCultureEvent,
                        physicalCultureEvent,
                        physicalCultureEvent,
                        physicalCultureEvent,
                        physicalCultureEvent
                    ), id = "1"),
                EventsOfDay(LocalDate.parse("2022-09-09"),
                    listOf(
                        physicalCultureEvent,
                        physicalCultureEvent,
                        physicalCultureEvent,
                        physicalCultureEvent,
                        physicalCultureEvent,
                        physicalCultureEvent,
                        physicalCultureEvent
                    ),
                    startsAtZero = true,
                    id = "1"),
                EventsOfDay(LocalDate.parse("2022-09-10"),
                    listOf(
                        physicalCultureEvent,
                        physicalCultureEvent,
                        physicalCultureEvent,
                        physicalCultureEvent,
                        physicalCultureEvent
                    ), id = "1")
            )
        )
    }
}