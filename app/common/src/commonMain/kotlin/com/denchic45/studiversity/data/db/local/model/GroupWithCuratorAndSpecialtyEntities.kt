package com.denchic45.studiversity.data.db.local.model

import com.denchic45.studiversity.StudyGroupEntity
import com.denchic45.studiversity.SpecialtyEntity
import com.denchic45.studiversity.UserEntity

class GroupWithCuratorAndSpecialtyEntities(
    val groupEntity: StudyGroupEntity,
    val curatorEntity: UserEntity,
    val specialtyEntity: SpecialtyEntity
)