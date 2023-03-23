package com.denchic45.kts.ui.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.denchic45.kts.ui.timetable.TimetableFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject


class MainFragmentFactory constructor(
    private val someString: String
): FragmentFactory(){

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            TimetableFragment::class.java.name -> {
                val fragment = TimetableFragment(someString)
                fragment
            }

            else -> super.instantiate(classLoader, className)
        }
    }
}