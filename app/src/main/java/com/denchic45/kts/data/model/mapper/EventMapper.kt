package com.denchic45.kts.data.model.mapper

import com.denchic45.kts.data.domain.model.EventType
import com.denchic45.kts.data.model.room.EventEntity
import com.denchic45.kts.data.model.room.EventWithSubjectAndGroupAndTeachersEntities
import com.denchic45.kts.data.model.room.GroupEntity
import com.denchic45.kts.data.model.room.TeacherEventCrossRef
import com.denchic45.kts.data.remote.model.EventDetailsDoc
import com.denchic45.kts.data.remote.model.EventDetailsDoc.Companion.createEmpty
import com.denchic45.kts.data.remote.model.EventDoc
import com.denchic45.kts.domain.model.*
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import java.time.LocalDate

@Mapper(uses = [CourseContentMapper::class, UserMapper::class, GroupMapper::class, SubjectMapper::class])
abstract class EventMapper {

//    @Mapping(source = "entities.groupEntity", target = ".")
//    abstract fun groupWithCuratorAndSpecialtyEntityToCourseGroup(entity: GroupEntity): GroupHeader

//    @Named("mapDetails")
//    fun mapDetails(entities: EventWithSubjectAndGroupAndTeachersEntities): EventDetails {
//        return when (entities.eventEntity.eventType) {
//            EventType.LESSON -> eventEntityToLesson(entities)
//            EventType.SIMPLE -> eventEntityToSimple(entities)
//            EventType.EMPTY -> eventEntityToEmpty(entities)
//        }
//    }

//    @Mapping(source = "teacherEntities", target = "teachers")
//    @Mapping(source = "subjectEntity", target = "subject")
//    abstract fun eventEntityToLesson(entities: EventWithSubjectAndGroupAndTeachersEntities): Lesson
//
//    @Mapping(source = "eventEntity", target = ".")
//    abstract fun eventEntityToSimple(entities: EventWithSubjectAndGroupAndTeachersEntities): SimpleEventDetails
//
//    abstract fun eventEntityToEmpty(entities: EventWithSubjectAndGroupAndTeachersEntities): EmptyEventDetails

//    fun entityToDomain(eventWithSubjectAndTeachersEntities: List<EventWithSubjectAndGroupAndTeachersEntities>): List<Event> {
//        return eventWithSubjectAndTeachersEntities
//            .map { eventEntity: EventWithSubjectAndGroupAndTeachersEntities ->
//                eventEntityToEvent(eventEntity)
//            }
//    }

//    fun entitiesToEventsOfDay(
//        eventWithSubjectAndTeachersEntities: List<EventWithSubjectAndGroupAndTeachersEntities>,
//        date: LocalDate
//    ): EventsOfDay {
//        return EventsOfDay(date, entityToDomain(eventWithSubjectAndTeachersEntities))
//    }

//    @Mapping(source = "groupEntity", target = "groupHeader")
//    @Mapping(source = "eventEntity", target = ".")
//    @Mapping(target = "details", qualifiedByName = ["mapDetails"], source = ".")
//    abstract fun eventEntityToEvent(eventEntity: EventWithSubjectAndGroupAndTeachersEntities): Event

    @Named("mapTeacherList")
    fun mapTeacherList(teachers: List<User>): List<String> {
        return teachers.map(User::id)
    }

    fun docToEntity(docs: List<EventDoc>, dayId: String): List<EventEntity> {
        return docs.map { eventDoc ->
            docToEntity(eventDoc, dayId)
        }
    }

    fun domainToDoc(domain: Event, index: Int): EventDoc {
        val eventDoc = eventToEventDoc(domain, index)
        eventDoc.eventDetailsDoc = detailsToDetailsDoc(domain.details)
        return eventDoc
    }

    @Mapping(source = "domain.groupHeader.id", target = "groupId")
    @Mapping(
        source = "domain.details",
        target = "eventDetailsDoc",
        qualifiedByName = ["detailsToDetailsDoc"]
    )
    abstract fun eventToEventDoc(domain: Event, position: Int): EventDoc

    fun domainToDoc(docs: List<Event>): List<EventDoc> {
        return docs.withIndex()
            .map { (index, event) -> this.domainToDoc(event, index) }
    }

    @Named("detailsToDetailsDoc")
    fun detailsToDetailsDoc(eventDetails: EventDetails): EventDetailsDoc {
        return when (eventDetails.eventType) {
            EventType.LESSON -> lessonToDetailsDoc(eventDetails as Lesson)
            EventType.SIMPLE -> simpleToDetailsDoc(eventDetails as SimpleEventDetails)
            EventType.EMPTY -> emptyToDetailsDoc()
        }
    }

    @Mapping(source = "teachers", target = "teacherIds", qualifiedByName = ["mapTeacherList"])
    @Mapping(source = "subject.id", target = "subjectId")
    abstract fun lessonToDetailsDoc(eventDetails: Lesson): EventDetailsDoc

    abstract fun simpleToDetailsDoc(
        simpleEventDetails: SimpleEventDetails
    ): EventDetailsDoc

    private fun emptyToDetailsDoc(): EventDetailsDoc {
        return createEmpty()
    }

    @Mapping(source = "dayId", target = "dayId")
    @Mapping(source = "doc.eventDetailsDoc", target = ".")
    abstract fun docToEntity(doc: EventDoc, dayId: String): EventEntity

    abstract fun entityToDoc(entity: EventEntity): EventDoc

    fun lessonEntitiesToTeacherLessonCrossRefEntities(eventEntities: List<EventEntity>): List<TeacherEventCrossRef> {
        return eventEntities
            .filter { !it.teacherIds.isNullOrEmpty() }
            .flatMap { eventEntity ->
                eventEntity.teacherIds!!.map { id: String ->
                    TeacherEventCrossRef(
                        eventEntity.id,
                        id
                    )
                }
            }
    }
}