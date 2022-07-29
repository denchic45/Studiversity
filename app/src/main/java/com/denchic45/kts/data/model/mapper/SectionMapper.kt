package com.denchic45.kts.data.model.mapper

import com.denchic45.kts.domain.model.Section
import com.denchic45.kts.data.remote.model.SectionMap
import com.denchic45.kts.data.model.room.SectionEntity
import org.mapstruct.Mapper
import org.mapstruct.Named

@Mapper
abstract class SectionMapper {

    @Named("entityToDomain")
    fun entityToDomain(entity: SectionEntity?): Section {
        return entity?.let {
            sectionEntityToSectionDomain(it)
        } ?: Section.createEmpty()
    }

    abstract fun sectionEntityToSectionDomain(entity: SectionEntity): Section

    abstract fun entityToDomain(entityList: List<SectionEntity>): List<Section>

    abstract fun docToEntity(sectionMap: SectionMap): SectionEntity

    abstract fun docToEntity(sectionDocs: List<SectionMap>): List<SectionEntity>
}