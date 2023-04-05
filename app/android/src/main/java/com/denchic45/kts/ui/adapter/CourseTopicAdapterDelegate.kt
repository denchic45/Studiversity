package com.denchic45.kts.ui.adapter

import android.view.ViewGroup
import com.denchic45.kts.databinding.ItemCourseTopicBinding
import com.denchic45.kts.util.viewBinding
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
import com.denchic45.widget.extendedAdapter.ListItemAdapterDelegate

class CourseTopicAdapterDelegate : ListItemAdapterDelegate<TopicResponse, TopicHolder>() {

    override fun isForViewType(item: Any): Boolean = item is TopicResponse

    override fun onBindViewHolder(item: TopicResponse, holder: TopicHolder) =
        holder.onBind(item)

    override fun onCreateViewHolder(parent: ViewGroup): TopicHolder {
        return TopicHolder(parent.viewBinding(ItemCourseTopicBinding::inflate))
    }
}

class TopicHolder(SectionBinding: ItemCourseTopicBinding) :
    BaseViewHolder<TopicResponse, ItemCourseTopicBinding>(SectionBinding) {
    override fun onBind(item: TopicResponse) {
        with(binding) {
            tvName.text = item.name
        }
    }
}