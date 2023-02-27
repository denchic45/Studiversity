package com.denchic45.kts.data.db.local.model

import com.denchic45.kts.StudyGroupEntity
import com.denchic45.kts.SpecialtyEntity
import com.denchic45.kts.UserEntity

class GroupWithCuratorAndSpecialtyEntities(
    val groupEntity: StudyGroupEntity,
    val curatorEntity: UserEntity,
    val specialtyEntity: SpecialtyEntity
)