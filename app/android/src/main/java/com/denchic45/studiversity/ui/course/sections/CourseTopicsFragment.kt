package com.denchic45.studiversity.ui.course.sections

import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import by.kirich1409.viewbindingdelegate.viewBinding
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.denchic45.studiversity.R
import com.denchic45.studiversity.databinding.FragmentCourseTopicsBinding
import com.denchic45.studiversity.databinding.ItemAddSectionBinding
import com.denchic45.studiversity.databinding.ItemEditSectionBinding
import com.denchic45.studiversity.ui.adapter.BaseViewHolder
import com.denchic45.studiversity.ui.appbar.AppBarInteractor
import com.denchic45.studiversity.ui.base.HasNavArgs
import com.denchic45.studiversity.ui.coursetopics.CourseTopicsComponent
import com.denchic45.studiversity.ui.model.UiModel
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.studiversity.util.closeKeyboard
import com.denchic45.studiversity.util.collectWhenStarted
import com.denchic45.studiversity.util.repeatOnViewLifecycle
import com.denchic45.studiversity.util.showKeyboard
import com.denchic45.studiversity.util.viewBinding
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
import com.denchic45.stuiversity.util.toUUID
import com.denchic45.studiversity.widget.extendedAdapter.ListItemAdapterDelegate
import com.denchic45.studiversity.widget.extendedAdapter.adapter
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CourseTopicsFragment(
    private val appBarInteractor: AppBarInteractor,
    private val courseTopicsComponent: (UUID, ComponentContext) -> CourseTopicsComponent,
) : Fragment(R.layout.fragment_course_topics), HasNavArgs<CourseTopicsFragmentArgs> {


    val binding: FragmentCourseTopicsBinding by viewBinding(
        FragmentCourseTopicsBinding::bind
    )

    override val navArgs: CourseTopicsFragmentArgs by navArgs()

    val component: CourseTopicsComponent by lazy {
        courseTopicsComponent(
            navArgs.courseId.toUUID(),
            defaultComponentContext(requireActivity().onBackPressedDispatcher)
        )
    }

    companion object {
        const val COURSE_ID = "CourseSectionEditor COURSE_ID"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repeatOnViewLifecycle(Lifecycle.State.STARTED) {
            appBarInteractor.update { it.copy(title = uiTextOf("Темы")) }
        }

        val adapter = adapter {
            delegates(
                EditSectionAdapterDelegate(
                    renameCallback = { position, name ->
                        component.onTopicRename(position, name)
                    },
                    removeCallback = { component.onTopicRemove(it) },
                ), AddSectionAdapterDelegate(component::onTopicAdd)
            )
            extensions {

            }
        }
        with(binding) {
            val simpleCallback = object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val move = viewHolder is EditSectionAdapterDelegate.EditSectionHolder
                            && target is EditSectionAdapterDelegate.EditSectionHolder

                    if (move) {
                        val oldPosition = viewHolder.absoluteAdapterPosition
                        val position = target.absoluteAdapterPosition
                        component.onTopicMove(oldPosition - 1, position - 1)
                    }
                    return move
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                    viewHolder.itemView.isSelected =
                        actionState == ItemTouchHelper.ACTION_STATE_DRAG && isCurrentlyActive
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    component.onSectionMoved()
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
            }
            val itemTouchHelper = ItemTouchHelper(simpleCallback)

            itemTouchHelper.attachToRecyclerView(rvSections)

            rvSections.adapter = adapter
            (rvSections.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            component.topics.collectWhenStarted(viewLifecycleOwner) {
                adapter.submit(listOf(EditSectionAdapterDelegate.AddSectionItem) + it)
            }

        }
    }
}

class EditSectionAdapterDelegate(
    private val renameCallback: (position: Int, name: String) -> Unit,
    private val removeCallback: (position: Int) -> Unit
) :
    ListItemAdapterDelegate<TopicResponse, EditSectionAdapterDelegate.EditSectionHolder>() {

    object AddSectionItem : UiModel {
        override fun equals(other: Any?): Boolean = other is AddSectionItem
    }

    override fun isForViewType(item: Any): Boolean = item is TopicResponse

    override fun onBindViewHolder(item: TopicResponse, holder: EditSectionHolder) =
        holder.onBind(item)

    override fun onCreateViewHolder(parent: ViewGroup): EditSectionHolder {
        return EditSectionHolder(
            parent.viewBinding(ItemEditSectionBinding::inflate),
            renameCallback,
            removeCallback
        )
    }

    class EditSectionHolder(
        itemEditSectionBinding: ItemEditSectionBinding,
        private val renameCallback: (position: Int, name: String) -> Unit,
        private val removeCallback: (position: Int) -> Unit
    ) :
        BaseViewHolder<TopicResponse, ItemEditSectionBinding>(itemEditSectionBinding) {
        override fun onBind(item: TopicResponse) {
            with(binding) {

                fun closeEditing() {
                    vsName.displayedChild = 0
                    vsRemove.displayedChild = 0
                    vsEditWithDone.displayedChild = 0
                    etName.closeKeyboard()
                }

                tvName.text = item.name
                etName.setText(item.name)

                etName.setOnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        closeEditing()
                    }
                }

                ivEdit.setOnClickListener {
                    vsName.displayedChild = 1
                    vsRemove.displayedChild = 1
                    vsEditWithDone.displayedChild = 1
                    etName.showKeyboard()
                    etName.setSelection(etName.length())
                }

                ivDone.setOnClickListener {
                    tvName.text = etName.text.toString()
                    closeEditing()
                    renameCallback(absoluteAdapterPosition - 1, etName.text.toString())
                }

                ivRemove.setOnClickListener {
                    closeEditing()
                    removeCallback(absoluteAdapterPosition - 1)
                }
            }
        }


    }
}

class AddSectionAdapterDelegate(private val doneCallback: (name: String) -> Unit) :
    ListItemAdapterDelegate<EditSectionAdapterDelegate.AddSectionItem, AddSectionAdapterDelegate.AddSectionHolder>() {
    class AddSectionHolder(
        itemAddSectionBinding: ItemAddSectionBinding,
        private val doneCallback: (name: String) -> Unit
    ) :
        BaseViewHolder<EditSectionAdapterDelegate.AddSectionItem, ItemAddSectionBinding>(
            itemAddSectionBinding
        ) {
        override fun onBind(item: EditSectionAdapterDelegate.AddSectionItem) {
            with(binding) {
                fun closeEditing() {
                    vs.displayedChild = 0
                    etName.setText("")
                    ivDone.visibility = View.INVISIBLE
                    etName.closeKeyboard()
                }
                etName.setOnFocusChangeListener { _, focus ->
                    if (focus) {
                        vs.displayedChild = 1
                        ivDone.visibility = View.VISIBLE
                    } else {
                        closeEditing()
                    }
                }
                ivClose.setOnClickListener {
                    etName.closeKeyboard()
                }
                ivAdd.setOnClickListener {

                    etName.showKeyboard()
                }
                ivDone.setOnClickListener {
                    doneCallback(etName.text.toString())
                    closeEditing()
                }
            }
        }


    }

    override fun isForViewType(item: Any): Boolean =
        item is EditSectionAdapterDelegate.AddSectionItem

    override fun onBindViewHolder(
        item: EditSectionAdapterDelegate.AddSectionItem,
        holder: AddSectionHolder
    ) = holder.onBind(item)

    override fun onCreateViewHolder(parent: ViewGroup): AddSectionHolder {
        return AddSectionHolder(parent.viewBinding(ItemAddSectionBinding::inflate), doneCallback)
    }
}