package com.denchic45.kts.ui.adapter

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.denchic45.kts.R
import com.denchic45.kts.databinding.ItemIconContentBinding
import com.denchic45.kts.util.viewBinding
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.widget.extendedAdapter.ListItemAdapterDelegate

class StudyGroupAdapterDelegate : ListItemAdapterDelegate<StudyGroupResponse, GroupHolder>() {

    override fun isForViewType(item: Any): Boolean = item is StudyGroupResponse

    override fun onBindViewHolder(item: StudyGroupResponse, holder: GroupHolder) =
        holder.onBind(item)

    override fun onCreateViewHolder(parent: ViewGroup): GroupHolder {
        return GroupHolder(parent.viewBinding(ItemIconContentBinding::inflate))
    }
}

class GroupHolder(
    itemIconContentBinding: ItemIconContentBinding
) : BaseViewHolder<StudyGroupResponse, ItemIconContentBinding>(
    itemIconContentBinding
) {
    private val tvName: TextView = itemView.findViewById(R.id.tv_name)
    private val ivIcon: ImageView = itemView.findViewById(R.id.iv_icon)
    override fun onBind(item: StudyGroupResponse) {
        tvName.text = item.name
    }

    init {
        ivIcon.setImageDrawable(
            ContextCompat.getDrawable(itemView.context, R.drawable.ic_study_group)
        )
    }
}