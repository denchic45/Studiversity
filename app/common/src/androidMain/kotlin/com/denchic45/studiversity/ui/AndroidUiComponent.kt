package com.denchic45.studiversity.ui

import androidx.navigation.NavDirections
import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

interface AndroidUiComponent: BaseUiComponent {

    val finish: MutableSharedFlow<Nothing?>

    val toast: MutableSharedFlow<String>

    val toastRes: MutableSharedFlow<Int>

    val snackBar: MutableSharedFlow<Pair<String, String?>>

    val snackBarRes: MutableSharedFlow<Pair<Int, Int?>>

    val dialog: MutableSharedFlow<Pair<String, String>>

    val dialogRes: MutableSharedFlow<Pair<Int, Int>>

    val _openConfirmation: Channel<Pair<String, String>>

    val openConfirmation: Flow<Pair<String, String>>

    val showToolbarTitle: MutableSharedFlow<String>

    val navigate: MutableSharedFlow<NavigationCommand>

    fun navigateTo(navDirections: NavDirections) {
        componentScope.launch {
            navigate.emit(NavigationCommand.To(navDirections))
        }
    }

    val optionsVisibility: MutableStateFlow<Map<Int, Boolean>>

    fun setMenuItemVisible(itemIdWithVisible: Pair<Int, Boolean>) {
        optionsVisibility.update { it + itemIdWithVisible }
    }

    fun setMenuItemVisible(vararg itemIdWithVisible: Pair<Int, Boolean>) {
        optionsVisibility.update { it + itemIdWithVisible }
    }

//    var toolbarTitle: String
//        get() = showToolbarTitle.replayCache[0]
//        set(value) {
//            showToolbarTitle.tryEmit(value)
//        }

    fun setTitle(name: String) {
        showToolbarTitle.tryEmit(name)
    }

     suspend fun finish() {
        finish.emit(null)
    }

     fun openConfirmation(titleWithText: Pair<String, String>) {
        componentScope.launch {
            _openConfirmation.send(titleWithText)
        }
    }

     fun showToast(message: String) {
        componentScope.launch { toast.emit(message) }
    }

     fun showToast(messageRes: Int) {
        componentScope.launch { toastRes.emit(messageRes) }
    }

     fun showSnackBar(message: String, action: String? = null) {
        componentScope.launch { snackBar.emit(message to action) }
    }

     fun showSnackBar(messageRes: Int, actionRes: Int? = null) {
        componentScope.launch { snackBarRes.emit(messageRes to actionRes) }
    }

     fun showDialog(title: String, message: String) {
        componentScope.launch { dialog.emit(Pair(title, message)) }
    }

     fun showDialog(title: Int, message: Int) {
        componentScope.launch { dialogRes.emit(Pair(title, message)) }
    }

    var optionsIsCreated: Boolean

    open fun onCreateOptions() {
        if (optionsIsCreated) return
        else optionsIsCreated = true
    }

    open fun onOptionClick(itemId: Int) {}

    open fun onSnackbarActionClick(message: String) {}
}

class AndroidUiComponentDelegate(componentContext: ComponentContext) :
    ComponentContext by componentContext, AndroidUiComponent {

    override val componentScope = componentScope()

    override val finish = MutableSharedFlow<Nothing?>()

    override val toast = MutableSharedFlow<String>()

    override val toastRes = MutableSharedFlow<Int>()

    override val snackBar = MutableSharedFlow<Pair<String, String?>>()

    override val snackBarRes = MutableSharedFlow<Pair<Int, Int?>>()

    override val dialog = MutableSharedFlow<Pair<String, String>>()

    override val dialogRes = MutableSharedFlow<Pair<Int, Int>>()

    override val _openConfirmation = Channel<Pair<String, String>>()

    override val openConfirmation = _openConfirmation.receiveAsFlow()

    override val showToolbarTitle = MutableSharedFlow<String>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val navigate = MutableSharedFlow<NavigationCommand>()

    override val optionsVisibility = MutableStateFlow<Map<Int, Boolean>>(emptyMap())

    override var optionsIsCreated = false
}

