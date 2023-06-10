package com.denchic45.studiversity.ui.tasks

import com.denchic45.studiversity.ui.base.BaseViewModel
import javax.inject.Inject

class TasksViewModel @Inject constructor(

) : BaseViewModel() {

    val tabNames: Array<String> = arrayOf("Предстоящие","Просроченные","Выполненные")


}