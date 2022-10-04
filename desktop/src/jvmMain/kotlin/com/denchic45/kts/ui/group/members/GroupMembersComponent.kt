package com.denchic45.kts.ui.group.members

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.ui.group.GroupComponent
import com.denchic45.kts.ui.timetable.TimetableComponent
import me.tatarka.inject.annotations.Inject

@Inject
class GroupMembersComponent(

    componentContext: ComponentContext) :
    ComponentContext by componentContext