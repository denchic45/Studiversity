package com.denchic45.studiversity.ui

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.denchic45.studiversity.common.R
import com.denchic45.studiversity.util.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.flow.debounce

interface HasComponent<C : AndroidUiComponent> {
    val component: C
}

abstract class BaseFragment2<C : AndroidUiComponent, VB : ViewBinding>(
    layoutId: Int,
    private val menuResId: Int = 0,
) : Fragment(layoutId), HasComponent<C> {

    abstract val binding: VB

    open val navController: NavController by lazy { findNavController() }

    lateinit var menu: Menu
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (menuResId != 0)
            setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        component.toast.collectWhenStarted(viewLifecycleOwner, this@BaseFragment2::toast)

        collectOnShowToolbarTitle()

        collectOnOptionVisibility()

        component.navigate.collectWhenResumed(viewLifecycleOwner) { command ->
            when (command) {
                is NavigationCommand.To -> navController.navigate(command.directions)
                NavigationCommand.Back -> navController.popBackStack()
                is NavigationCommand.BackTo ->
                    navController.popBackStack(command.destinationId, false)

                NavigationCommand.ToRoot ->
                    navController.popBackStack(navController.graph.startDestinationId, false)
            }
        }

        component.finish.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
            .collectWhenStarted(viewLifecycleOwner) {
                findNavController().navigateUp()
            }

        component.toast.collectWhenStarted(viewLifecycleOwner, this::toast)

        component.toastRes.collectWhenStarted(viewLifecycleOwner, this::toast)

        component.snackBar.collectWhenStarted(viewLifecycleOwner) { (message, action) ->
            val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            action?.let { snackbar.setAction(action) { component.onSnackbarActionClick(message) } }
            snackbar.show()
        }

        component.snackBarRes.collectWhenStarted(viewLifecycleOwner) { (messageRes, action) ->
            val snackbar = Snackbar.make(binding.root, messageRes, Snackbar.LENGTH_LONG)
            action?.let {
                snackbar.setAction(action) {
                    component.onSnackbarActionClick(requireContext().strings(messageRes))
                }
            }
            snackbar.show()
        }

        component.dialog.collectWhenStarted(viewLifecycleOwner) { (title, message) ->
            MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
                .setTitle(title)
                .setMessage(message)
                .create()
                .show()
        }

        component.dialogRes.collectWhenStarted(viewLifecycleOwner) { (titleRes, messageRes) ->
            MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
                .setTitle(titleRes)
                .setMessage(messageRes)
                .create()
                .show()
        }

//        component.openConfirmation.collectWhenStarted(viewLifecycleOwner) { (title, message) ->
//            navController.navigate(
//                R.id.action_global_confirmDialog,
//                bundleOf(
//                    ConfirmDialog.TITLE to title,
//                    ConfirmDialog.MESSAGE to message
//                )
//            )
//        }
    }

    open fun collectOnOptionVisibility() {
        component.optionsVisibility
            .debounce(500)
            .collectWhenResumed(viewLifecycleOwner) { optionsVisibility ->
                optionsVisibility.forEach { (itemId, visible) ->
                    menu.findItem(itemId).isVisible = visible
                }
            }
    }

    open fun collectOnShowToolbarTitle() {
        component.showToolbarTitle.collectWhenResumed(viewLifecycleOwner) {
            setActivityTitle(it)
        }
    }


    @Deprecated("Deprecated in Java")
    final override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
        inflater.inflate(menuResId, menu)
        component.onCreateOptions()
    }

    @Deprecated("Deprecated in Java")
    override fun onPrepareOptionsMenu(menu: Menu) {
        this.menu = menu
        return super.onPrepareOptionsMenu(menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        component.onOptionClick(item.itemId)
        return true
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}

sealed class NavigationCommand {
    data class To(val directions: NavDirections) : NavigationCommand()
    object Back : NavigationCommand()
    data class BackTo(val destinationId: Int) : NavigationCommand()
    object ToRoot : NavigationCommand()
}
