package com.denchic45.studiversity.ui.adapter

import android.view.ViewGroup
import com.denchic45.studiversity.databinding.ItemCourseTopicBinding
import com.denchic45.studiversity.util.viewBinding
import com.denchic45.studiversity.widget.extendedAdapter.ListItemAdapterDelegate
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse

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