package com.denchic45.kts.ui.creator

import androidx.lifecycle.viewModelScope
import com.denchic45.kts.MobileNavigationDirections
import com.denchic45.kts.R
import com.denchic45.kts.SingleLiveData
import com.denchic45.kts.data.model.domain.ListItem
import com.denchic45.kts.ui.UiImage
import com.denchic45.kts.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreatorViewModel @Inject constructor() : BaseViewModel() {
    var openGroupEditor = SingleLiveData<Void>()
    var openSubjectEditor = SingleLiveData<Void>()
    var openSpecialtyEditor = SingleLiveData<Void>()
    var openCourseEditor = SingleLiveData<Void>()
    fun onEntityClick(position: Int) {
        when (position) {
            0 -> {
                navigateTo(
                    MobileNavigationDirections.actionGlobalUserEditorFragment(null)
                )
            }
            1 -> openGroupEditor.call()
            2 -> openSubjectEditor.call()
            3 -> openSpecialtyEditor.call()
            4 -> openCourseEditor.call()
        }
        viewModelScope.launch { finish() }
    }

    fun createEntityList(): List<ListItem> {
        return listOf(
            ListItem(title = "Пользователя", icon = UiImage.IdImage(R.drawable.ic_user)),
            ListItem(title = "Группу", icon = UiImage.IdImage(R.drawable.ic_study_group)),
            ListItem(title = "Предмет", icon = UiImage.IdImage(R.drawable.ic_subject)),
            ListItem(title = "Специальность", icon = UiImage.IdImage(R.drawable.ic_specialty)),
            ListItem(title = "Курс", icon = UiImage.IdImage(R.drawable.ic_course))
        )
    }
}