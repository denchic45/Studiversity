package com.denchic45.studiversity.ui.adminPanel.timetableEditor.eventEditor.simpleEventEditor

//import androidx.lifecycle.MutableLiveData
//import com.denchic45.studiversity.common.R
//import com.denchic45.studiversity.SingleLiveData
//import com.denchic45.studiversity.data.domain.model.EventType
//import com.denchic45.studiversity.data.model.domain.ListItem
//import com.denchic45.studiversity.ui.UiColor
//import com.denchic45.studiversity.ui.model.UiImage
//import com.denchic45.studiversity.domain.model.SimpleEventDetails
//import com.denchic45.studiversity.ui.adminPanel.timetableEditor.eventEditor.EventEditorInteractor
//import com.denchic45.studiversity.ui.base.BaseViewModel
//import com.denchic45.studiversity.uivalidator.Rule
//import com.denchic45.studiversity.uivalidator.UIValidator
//import com.denchic45.studiversity.uivalidator.Validation
//import javax.inject.Inject
//
//class SimpleEventEditorViewModel @Inject constructor(
//    private var interactor: EventEditorInteractor
//) : BaseViewModel() {
//
//    val showSelectedEvent = MutableLiveData<SimpleEventDetails>()
//
//    val showEvents = SingleLiveData<List<ListItem>>()
//
//    val showErrorField = MutableLiveData<Pair<Int, Boolean>>()
//
//    private val events = listOf(
//        SimpleEventDetails.dinner(),
//        SimpleEventDetails.practice()
//    )
//
//    fun onEventClick() {
//        showEvents.value = mapEventsToListItems()
//    }
//
//    private fun mapEventsToListItems(): List<ListItem> {
//        return events
//            .map { eventDetails: SimpleEventDetails ->
//                ListItem(
//                    title = eventDetails.name, color = UiColor.ColorName(eventDetails.color),
//                    icon = UiImage.Url(eventDetails.iconUrl),
//                )
//            } + ListItem(title = "Создать", icon= UiImage.IdImage(R.drawable.ic_add))
//    }
//
//    fun onEventSelect(position: Int) {
//        val selectedEvent = events[position]
//        interactor.getDetails = { selectedEvent }
//        showSelectedEvent.value = selectedEvent
//    }
//
//    init {
//        with(interactor.oldEvent.value!!.details) {
//            if (this.eventType == EventType.SIMPLE) {
//                interactor.getDetails = { this as SimpleEventDetails }
//                showSelectedEvent.value = this as SimpleEventDetails
//            }
//        }
//
//        interactor.validateEventDetails = {
//            UIValidator.of(
//                Validation(Rule { showSelectedEvent.value != null })
//                    .sendActionResult({
//                        showErrorField.setValue(
//                            R.id.rl_event to true
//                        )
//                    }) {
//                        showErrorField.setValue(
//                            R.id.rl_event to false
//                        )
//                    }
//            ).runValidates()
//        }
//    }
//}