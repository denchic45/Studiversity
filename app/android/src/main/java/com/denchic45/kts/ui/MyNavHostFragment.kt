package com.denchic45.kts.ui

import android.content.Context
import androidx.navigation.fragment.NavHostFragment
import com.denchic45.kts.app

class MyNavHostFragment : NavHostFragment() {
    private val injectFragmentFactory = app.appComponent.injectFragmentFactory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        childFragmentManager.fragmentFactory = injectFragmentFactory
    }
}