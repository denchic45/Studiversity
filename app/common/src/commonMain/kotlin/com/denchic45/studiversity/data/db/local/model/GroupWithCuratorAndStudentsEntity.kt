package com.denchic45.studiversity.data.db.local.model

import com.denchic45.studiversity.StudyGroupEntity
import com.denchic45.studiversity.UserEntity

class GroupWithCuratorAndStudentsEntity(
    val groupEntity: StudyGroupEntity,
    val curatorEntity: UserEntity,
    val studentEntities: List<UserEntity>
)