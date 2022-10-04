package com.denchic45.kts.ui.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.denchic45.kts.R
import com.denchic45.kts.di.viewmodel.ViewModelFactory
import com.denchic45.kts.ui.main.MainActivity
import com.denchic45.kts.util.closeKeyboard
import com.denchic45.kts.util.findFragmentContainerNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.richpath.RichPath
import com.richpath.RichPathView
import dagger.android.AndroidInjection
import javax.inject.Inject

class LoginActivity : AppCompatActivity(R.layout.activity_login) {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory<LoginViewModel>
    private val viewModel: LoginViewModel by viewModels { viewModelFactory }
    private var linePath: RichPath? = null
    private lateinit var fabBack: FloatingActionButton
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        navController = findFragmentContainerNavController(R.id.nav_host_fragment)
        val rpProgressAuth = findViewById<RichPathView>(R.id.richPathView_progressAuth)
        fabBack = findViewById(R.id.fab_back)
        linePath = rpProgressAuth.findRichPathByName("line")
        fabBack.setOnClickListener {
            viewModel.onFabBackClick(
                navController.currentDestination!!.id
            )
        }
        viewModel.openLoginByMail.observe(this) {
            navController.navigate(R.id.action_authFragment_to_loginByEmailFragment)
        }

        viewModel.backToFragment.observe(this) {
            super.onBackPressed()
            currentFocus?.closeKeyboard()
        }

        viewModel.fabVisibility.observe(
            this
        ) { aBoolean: Boolean -> if (aBoolean) fabBack.show() else fabBack.hide() }
        viewModel.openMain.observe(this) {
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }, 500
            )
        }

        viewModel.finishApp.observe(this) { finish() }
    }

    override fun onBackPressed() {
        viewModel.onFabBackClick(navController.currentDestination!!.id)
    }
}