package com.denchic45.studiversity.ui.base

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.viewbinding.ViewBinding
import com.denchic45.studiversity.R
import com.denchic45.studiversity.di.viewmodel.ViewModelFactory
import com.denchic45.studiversity.ui.NavigationCommand
import com.denchic45.studiversity.ui.confirm.ConfirmDialog
import com.denchic45.studiversity.util.collectWhenStarted
import com.denchic45.studiversity.util.strings
import com.denchic45.studiversity.util.toast
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import javax.inject.Inject

abstract class BaseActivity<VM : BaseViewModel, VB : ViewBinding>(
    layoutId: Int = 0
) : AppCompatActivity(layoutId) {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<VM>

    abstract val viewModel: VM

    abstract val binding: VB

    open val navController: NavController by lazy { findNavController(R.id.nav_host_fragment) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)

        viewModel.navigate.collectWhenStarted(this) { command ->
            when (command) {
                is NavigationCommand.To -> navController.navigate(command.directions)
                NavigationCommand.Back -> navController.popBackStack()
                is NavigationCommand.BackTo ->
                    navController.popBackStack(command.destinationId, false)
                NavigationCommand.ToRoot ->
                    navController.popBackStack(navController.graph.startDestinationId, false)
            }
        }

        viewModel.finish.collectWhenStarted(this) {
            finish()
        }

        viewModel.toast.collectWhenStarted(this) {
            toast(it)
            Log.d("lol", "showToast: $it")
        }

        viewModel.toastRes.collectWhenStarted(this, this::toast)

        viewModel.snackBar.collectWhenStarted(this) { (message, action) ->
            val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            action?.let { snackbar.setAction(action) { viewModel.onSnackbarActionClick(message) } }
            snackbar.show()
        }

        viewModel.snackBarRes.collectWhenStarted(this) { (messageRes, action) ->
            val snackbar = Snackbar.make(binding.root, messageRes, Snackbar.LENGTH_LONG)
            action?.let {
                snackbar.setAction(action) {
                    viewModel.onSnackbarActionClick(strings(messageRes))
                }
            }
            snackbar.show()
        }

        viewModel.openConfirmation.collectWhenStarted(this) { (title, message) ->
            navController.navigate(
                R.id.action_global_confirmDialog,
                bundleOf(
                    ConfirmDialog.TITLE to title,
                    ConfirmDialog.MESSAGE to message
                )
            )
        }
    }
}