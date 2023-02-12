package com.denchic45.kts.data.db.remote.source

import com.denchic45.firebasemultiplatform.api.*
import com.denchic45.firebasemultiplatform.ktor.runQuery
import com.denchic45.kts.data.db.remote.model.DayMap
import com.denchic45.kts.di.FirebaseHttpClient
import com.denchic45.kts.util.parseDocuments
import com.denchic45.kts.util.toTimestampValue
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.util.*

@me.tatarka.inject.annotations.Inject
actual class EventRemoteDataSource(private val client: FirebaseHttpClient) {

    actual fun observeEventsOfGroupByDate(groupId: String, date: LocalDate): Flow<DayMap?> {
        TODO("Not yet implemented")
    }

    actual suspend fun findEventsOfGroupByDate(
        groupId: String,
        date: LocalDate,
    ): DayMap {
        TODO("Not yet implemented")
    }

    actual suspend fun updateEventsOfDay(dayMap: DayMap) {
    }

    actual fun observeEventsOfTeacherByDate(
        teacherId: String,
        date: LocalDate,
    ): Flow<List<DayMap>> {
        TODO("Not yet implemented")
    }

    actual suspend fun setDay(dayMap: DayMap) {
    }

    actual suspend fun findEventsOfGroupByDateRange(
        groupId: String,
        previousMonday: Date,
        nextSaturday: Date,
    ): List<DayMap> {
        TODO("Not yet implemented")
    }

    actual fun observeEventsOfGroupByPreviousAndNextDates(
        groupId: String,
        previousMonday: Date,
        nextSaturday: Date,
    ): Flow<List<DayMap>> = flow {
        emit(
            client.runQuery(
                Request(
                    StructuredQuery(
                        from = CollectionSelector(collectionId = "Days", allDescendants = true),
                        where = Filter(
                            compositeFilter = CompositeFilter(
                                op = CompositeFilter.Operator.AND,
                                filters = listOf(
                                    Filter(
                                        fieldFilter = FieldFilter(
                                            field = FieldReference("date"),
                                            op = FieldFilter.Operator.GREATER_THAN_OR_EQUAL,
                                            value = Value(timestampValue = previousMonday.toTimestampValue())
                                        )
                                    ),
                                    Filter(
                                        fieldFilter = FieldFilter(
                                            field = FieldReference("date"),
                                            op = FieldFilter.Operator.LESS_THAN_OR_EQUAL,
                                            value = Value(timestampValue = nextSaturday.toTimestampValue())
                                        )
                                    ),
                                    Filter(
                                        fieldFilter = FieldFilter(
                                            field = FieldReference("groupId"),
                                            op = FieldFilter.Operator.EQUAL,
                                            value = Value(stringValue = groupId)
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            ).bodyAsText().let {
                parseDocuments(Json.parseToJsonElement(it), ::DayMap)
            }
        )
    }
}