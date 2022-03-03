package com.denchic45.kts.ui.timetable

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.denchic45.EventObserver
import com.denchic45.kts.R
import com.denchic45.kts.data.model.DomainModel
import com.denchic45.kts.databinding.FragmentTimetableBinding
import com.denchic45.kts.ui.BaseFragment
import com.denchic45.kts.ui.adapter.EventAdapter
import com.denchic45.kts.ui.adapter.EventAdapter.OnLessonItemClickListener
import com.denchic45.kts.ui.main.MainViewModel
import com.denchic45.widget.ListStateLayout
import com.denchic45.widget.calendar.WeekCalendarListener
import com.denchic45.widget.calendar.WeekCalendarListener.OnLoadListener
import com.denchic45.widget.calendar.model.WeekItem
import com.example.appbarcontroller.appbarcontroller.AppBarController
import kotlinx.coroutines.flow.collect
import java.time.LocalDate

class TimetableFragment :
    BaseFragment<TimetableViewModel, FragmentTimetableBinding>(R.layout.fragment_timetable),
    WeekCalendarListener,
    OnLoadListener {

    override val binding: FragmentTimetableBinding by viewBinding(FragmentTimetableBinding::bind)
    override val viewModel: TimetableViewModel by viewModels { viewModelFactory }
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var appBarController: AppBarController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_timtable, menu)
    }

    override fun onResume() {
        super.onResume()
//        viewModel.toolbarTitle.value = viewModel.toolbarTitle.value

        appBarController.setLiftOnScroll(false)
        appBarController.setExpandableIfViewCanScroll(this.binding.rvLessons, viewLifecycleOwner)
//        viewModel.toolbarTitle.observe(
//            viewLifecycleOwner
//        ) { title -> (requireActivity() as AppCompatActivity).supportActionBar!!.title = title }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(this.binding) {
            val listStateLayout: ListStateLayout = view.findViewById(R.id.listStateLayout)
            listStateLayout.addView(R.layout.state_lessons_day_off, DAY_OFF_VIEW)
            viewModel.initTimetable.observe(viewLifecycleOwner) { groupVisibility: Boolean ->
                appBarController = AppBarController.findController(
                    requireActivity()
                )

                binding.wcv.setWeekCalendarListener(this@TimetableFragment)

                lifecycleScope.launchWhenStarted {
                    viewModel.selectedDate.collect { selectDate ->
                        wcv.setSelectDate(selectDate)
                    }
                }
                val adapter = EventAdapter(viewModel.lessonTime, groupVisibility,

                    onLessonItemClickListener = object : OnLessonItemClickListener() {
                    })
                rvLessons.adapter = adapter
                viewModel.showLessonsOfDay.observe(
                    viewLifecycleOwner,
                    EventObserver { lessons ->
                        adapter.submitList(
                            ArrayList<DomainModel>(lessons),
                            listStateLayout.getCommitCallback(adapter)
                        )
                        rvLessons.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                startLiftOnScrollElevationOverlayAnimation(
                                    recyclerView.canScrollVertically(
                                        -1
                                    )
                                )
                            }
                        })
                    })
                viewModel.showListState.observe(
                    viewLifecycleOwner,
                    EventObserver { t ->
                        adapter.submitList(emptyList()) {
                            listStateLayout.showView(t)
                        }
                    })
            }


        }
    }



    private fun startLiftOnScrollElevationOverlayAnimation(lifted: Boolean) {
        val appBarElevation = 4F
        val fromElevation: Float = if (lifted) 0F else appBarElevation
        val toElevation: Float = if (lifted) appBarElevation else 0F
        if (lifted && this.binding.wcv.elevation == appBarElevation
            || !lifted && this.binding.wcv.elevation == 0F
        ) return
        val elevationOverlayAnimator = ValueAnimator.ofFloat(fromElevation, toElevation)
        elevationOverlayAnimator.duration = 150
        elevationOverlayAnimator.interpolator = LinearInterpolator()
        elevationOverlayAnimator.addUpdateListener { valueAnimator: ValueAnimator ->
            this.binding.wcv.elevation = valueAnimator.animatedValue as Float
        }
        elevationOverlayAnimator.start()
    }

    override fun onDaySelect(date: LocalDate) {
        viewModel.onDaySelect(date)
    }

    override fun onWeekSelect(weekItem: WeekItem) {
        viewModel.onWeekSelect(weekItem)
    }

    override fun onWeekLoad(weekItem: WeekItem) {
        viewModel.onWeekLoad(weekItem)
    }

    companion object {
        const val DAY_OFF_VIEW = "DAY_OFF_VIEW"
        const val GROUP_ID = "GROUP_ID"

        fun newInstance(groupId: String): TimetableFragment {
            val fragment = TimetableFragment()
            val args = Bundle()
            args.putString(GROUP_ID, groupId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.wcv.removeListeners()
    }
}