package com.denchic45.kts.ui.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.denchic45.kts.R
import com.denchic45.kts.ui.confirm.ConfirmDialog
import com.denchic45.kts.util.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.flow.debounce
import javax.inject.Inject

interface HasComponent<C : AndroidUiComponent> {
    var component: C
}

abstract class BaseFragment2<C : AndroidUiComponent, VB : ViewBinding>(
    layoutId: Int,
    private val menuResId: Int = 0
) : Fragment(layoutId), HasComponent<C> {

    abstract val binding: VB

    @Inject
    override lateinit var component: C

    open val navController: NavController by lazy { findNavController() }

    protected lateinit var menu: Menu
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (menuResId != 0)
            setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        component.toast.collectWhenStarted(lifecycleScope, this@BaseFragment2::toast)

        collectOnShowToolbarTitle()

        collectOnOptionVisibility()

        component.navigate.collectWhenResumed(viewLifecycleOwner.lifecycleScope) { command ->
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
            .collectWhenStarted(lifecycleScope) {
                Log.d("lol", "finish: ${this.javaClass.name}")
                findNavController().navigateUp()
            }

        component.toast.collectWhenStarted(viewLifecycleOwner.lifecycleScope, this::toast)

        component.toastRes.collectWhenStarted(viewLifecycleOwner.lifecycleScope, this::toast)

        component.snackBar.collectWhenStarted(viewLifecycleOwner.lifecycleScope) { (message, action) ->
            val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            action?.let { snackbar.setAction(action) { component.onSnackbarActionClick(message) } }
            snackbar.show()
        }

        component.snackBarRes.collectWhenStarted(viewLifecycleOwner.lifecycleScope) { (messageRes, action) ->
            val snackbar = Snackbar.make(binding.root, messageRes, Snackbar.LENGTH_LONG)
            action?.let {
                snackbar.setAction(action) {
                    component.onSnackbarActionClick(requireContext().strings(messageRes))
                }
            }
            snackbar.show()
        }

        component.dialog.collectWhenStarted(lifecycleScope) { (title, message) ->
            MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
                .setTitle(title)
                .setMessage(message)
                .create()
                .show()
        }

        component.dialogRes.collectWhenStarted(lifecycleScope) { (titleRes, messageRes) ->
            MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
                .setTitle(titleRes)
                .setMessage(messageRes)
                .create()
                .show()
        }

        component.openConfirmation.collectWhenStarted(lifecycleScope) { (title, message) ->
            navController.navigate(
                R.id.action_global_confirmDialog,
                bundleOf(
                    ConfirmDialog.TITLE to title,
                    ConfirmDialog.MESSAGE to message
                )
            )
        }
    }

    open fun collectOnOptionVisibility() {
        component.optionsVisibility
            .debounce(500)
            .collectWhenResumed(lifecycleScope) { optionsVisibility ->
                optionsVisibility.forEach { (itemId, visible) ->
                    menu.findItem(itemId).isVisible = visible
                }
            }
    }

    open fun collectOnShowToolbarTitle() {
        component.showToolbarTitle.collectWhenResumed(lifecycleScope) {
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