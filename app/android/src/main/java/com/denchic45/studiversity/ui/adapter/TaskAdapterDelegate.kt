package com.denchic45.studiversity.ui.adapter

import android.view.ViewGroup
import com.denchic45.studiversity.domain.model.Task
import com.denchic45.studiversity.databinding.ItemTaskBinding
import com.denchic45.studiversity.util.viewBinding
import com.denchic45.stuiversity.util.toString
import com.denchic45.studiversity.widget.extendedAdapter.ListItemAdapterDelegate

class TaskAdapterDelegate : ListItemAdapterDelegate<Task, TaskHolder>() {

    override fun isForViewType(item: Any): Boolean = item is Task

    override fun onBindViewHolder(item: Task, holder: TaskHolder) = holder.onBind(item)

    override fun onCreateViewHolder(parent: ViewGroup): TaskHolder {
        return TaskHolder(parent.viewBinding(ItemTaskBinding::inflate))
    }
}

class TaskHolder(itemTaskBinding: ItemTaskBinding) :
    BaseViewHolder<Task, ItemTaskBinding>(itemTaskBinding) {
    override fun onBind(item: Task) {
        with(binding) {
            tvTitle.text = item.name
            tvSubtitle.text = item.completionDate?.toString("dd MMM HH:mm")
                ?: run { "Без срока сдачи" }
        }
    }

}