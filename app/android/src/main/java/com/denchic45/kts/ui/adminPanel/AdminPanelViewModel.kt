package com.denchic45.kts.ui.adminPanel

import com.denchic45.kts.MobileNavigationDirections
import com.denchic45.kts.R
import com.denchic45.kts.SingleLiveData
import com.denchic45.kts.data.model.domain.ListItem
import com.denchic45.kts.ui.UiColor
import com.denchic45.kts.ui.UiImage
import com.denchic45.kts.ui.base.BaseViewModel
import javax.inject.Inject

class AdminPanelViewModel @Inject constructor() : BaseViewModel() {

//    var openTimetableEditor: SingleLiveData<*> = SingleLiveData<Any>()

//    var openUserFinder: SingleLiveData<*> = SingleLiveData<Any>()

    var openCreator: SingleLiveData<*> = SingleLiveData<Any>()
    val itemList: List<ListItem>
        get() = listOf(
            ListItem(
                title = "Найти что угодно",
                color = UiColor.ColorId(R.color.blue),
                icon= UiImage.IdImage(R.drawable.ic_search_)
            ),
            ListItem(
                title = "Добавить",
                color = UiColor.ColorId(R.color.blue),
                icon = UiImage.IdImage(R.drawable.ic_add)
            ),
            ListItem(
                title = "Расписания",
                color = UiColor.ColorId(R.color.blue),
                icon= UiImage.IdImage(R.drawable.ic_book)
            ),
            ListItem(
                title = "Создать расписание",
                color = UiColor.ColorId(R.color.blue),
                icon= UiImage.IdImage(R.drawable.ic_add)
            )
        )

    fun onItemClick(position: Int) {
        when (position) {
            0 -> navigateTo(AdminPanelFragmentDirections.actionMenuAdminPanelToFinderFragment())
            1 -> openCreator.call()
            2 -> navigateTo(AdminPanelFragmentDirections.actionMenuAdminPanelToTimetableFinderFragment())
            3 ->navigateTo(AdminPanelFragmentDirections.actionMenuAdminPanelToTimetableLoaderFragment())
        }
    }
}