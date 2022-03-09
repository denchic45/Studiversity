package com.denchic45.kts.data.model.mapper;

import com.denchic45.kts.data.model.domain.CourseGroup;
import com.denchic45.kts.data.model.domain.Group;
import com.denchic45.kts.data.model.firestore.GroupDoc;
import com.denchic45.kts.data.model.room.GroupEntity;
import com.denchic45.kts.data.model.room.GroupWithCuratorAndSpecialtyEntity;
import com.denchic45.kts.utils.SearchKeysGenerator;
import com.google.firebase.firestore.FieldValue;

import org.jetbrains.annotations.NotNull;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(uses = {UserMapper.class, SpecialtyMapper.class})
public interface GroupMapper extends DocEntityMapper<GroupDoc, GroupEntity> {

    @Mapping(target = "searchKeys", ignore = true)
    @Mapping(target = "allUsers", ignore = true)
    @Mapping(target = "students", ignore = true)
    GroupDoc domainToDoc(Group domain);

    @DoIgnore
    @Mapping(target = "specialty", source = "specialtyEntity")
    @Mapping(target = ".", source = "groupEntity")
    @Mapping(target = "curator", source = "curatorEntity")
    Group groupWithCuratorAndSpecialtyEntityToGroup(GroupWithCuratorAndSpecialtyEntity entity);

    default Group entityToDomain(GroupWithCuratorAndSpecialtyEntity entity) {
        if (entity == null)
            return Group.deleted();
        else
            return groupWithCuratorAndSpecialtyEntityToGroup(entity);
    }

    default Map<String, Object> domainToMap(@NotNull Group group) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", group.getId());
        map.put("name", group.getName());
        map.put("course", group.getCourse());
        map.put("specialtyId", group.getSpecialty().getId());
        map.put("timestamp", FieldValue.serverTimestamp());
        map.put("specialty", group.getSpecialty());
        map.put("curator", group.getCurator());
        return map;
    }

    @Mapping(source = "specialty.id", target = "specialtyId")
    CourseGroup docToCourseGroupDomain(GroupDoc groupDoc);

    List<CourseGroup> docToCourseGroupDomain(List<GroupDoc> groupDocs);

    @Mapping(source = "curator.id", target = "curatorId")
    @Mapping(source = "specialty.id", target = "specialtyId")
    @Override
    GroupEntity docToEntity(GroupDoc doc);

    @Mapping(target = "searchKeys", ignore = true)
    @Mapping(target = "allUsers", ignore = true)
    @Override
    GroupDoc entityToDoc(GroupEntity entity);
}