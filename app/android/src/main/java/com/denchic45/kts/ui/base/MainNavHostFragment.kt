package com.denchic45.kts.ui.base

import android.content.Context
import androidx.navigation.fragment.NavHostFragment
import com.denchic45.kts.AndroidApp

class MainNavHostFragment : NavHostFragment() {
    private val fragmentFactory = (requireContext().applicationContext as AndroidApp).appComponent

    override fun onAttach(context: Context) {
        super.onAttach(context)
        childFragmentManager.fragmentFactory = fragmentFactory
    }
}