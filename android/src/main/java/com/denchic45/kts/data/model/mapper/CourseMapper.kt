package com.denchic45.kts.data.model.mapper

import com.denchic45.kts.data.db.remote.model.CourseMap
import com.denchic45.kts.data.model.room.CourseEntity
import com.denchic45.kts.data.model.room.CourseWithSubjectAndTeacherEntities
import com.denchic45.kts.domain.model.Course
import org.mapstruct.Mapper

@Mapper(uses = [GroupMapper::class, UserMapper::class, SubjectMapper::class, SpecialtyMapper::class])
abstract class CourseMapper {

//    @Mapping(qualifiedByName = ["courseGroupToGroupId"], source = "groupHeaders", target = "groupIds")
//    abstract fun domainToDoc(course: Course): CourseMap

//    abstract fun docToDomain(doc: CourseMap): Course

//    abstract fun docToDomain(docs: List<CourseMap>): List<CourseHeader>

//    abstract fun docToDomain2(docs: List<CourseMap>): List<CourseHeader>

//    @Named("groupWithCuratorAndSpecialtyEntityToCourseGroup")
//    @Mapping(source = "entities.groupEntity", target = ".")
//    abstract fun groupWithCuratorAndSpecialtyEntityToCourseGroup(entities: GroupWithCuratorAndSpecialtyEntity): GroupHeader

//    @IterableMapping(qualifiedByName = ["groupWithCuratorAndSpecialtyEntityToCourseGroup"])
//    abstract fun groupWithCuratorAndSpecialtyEntityToCourseGroup(entities: List<GroupWithCuratorAndSpecialtyEntity>): List<GroupHeader>

//    @Mapping(source = "groupEntities", target = "groupHeaders")
//    @Mapping(source = "courseEntity.name", target = "name")
//    @Mapping(source = "courseEntity.id", target = "id")
//    @Mapping(source = "subjectEntity", target = "subject")
//    @Mapping(source = "teacherEntity", target = "teacher")
//    @Named("entityToDomain2")
//    abstract fun entityToDomain2(entity: CourseWithSubjectWithTeacherAndGroupsEntities): Course

//    @IterableMapping(qualifiedByName = ["entityToDomain2"])
//    abstract fun entityToDomain2(entity: List<CourseWithSubjectWithTeacherAndGroupsEntities>): List<Course>

//    @Mapping(source = "courseEntity.name", target = "name")
//    @Mapping(source = "courseEntity.id", target = "id")
//    @Mapping(source = "subjectEntity", target = "subject")
//    @Mapping(source = "teacherEntity", target = "teacher")
//    @Named("entityToDomainHeaders")
//    abstract fun entityToDomainHeaders(entity: CourseWithSubjectAndTeacherEntities): CourseHeader

//    @IterableMapping(qualifiedByName = ["entityToDomainHeaders"])
//    abstract fun entityToDomainHeaders(entity: List<CourseWithSubjectAndTeacherEntities>): List<CourseHeader>

//    @Mapping(source = "subject.id", target = "subjectId")
//    @Mapping(source = "teacher.id", target = "teacherId")
//    abstract fun docToEntity(doc: CourseMap): CourseEntity

    abstract fun docToEntity(doc: List<CourseMap>): List<CourseEntity>

    abstract fun entityToDomain(entities: List<CourseWithSubjectAndTeacherEntities>): List<Course>

//    @Mapping(source = "teacherEntity", target = "teacher")
//    @Mapping(source = "subjectEntity", target = "subject")
//    @Mapping(source = "courseEntity", target = ".")
//    abstract fun entityToDoc(entities: CourseWithSubjectAndTeacherEntities): CourseMap

//    @Named("courseGroupToGroupId")
//    fun courseGroupToGroupId(groupHeader: GroupHeader): String {
//        return groupHeader.id
//    }
}