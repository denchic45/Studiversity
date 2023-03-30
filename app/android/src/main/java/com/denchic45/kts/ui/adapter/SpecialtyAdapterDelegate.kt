package com.denchic45.kts.ui.adapter

import android.graphics.drawable.PictureDrawable
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.denchic45.kts.R
import com.denchic45.kts.SvgColorListener
import com.denchic45.kts.databinding.ItemIconContentBinding
import com.denchic45.kts.glideSvg.GlideApp
import com.denchic45.kts.util.viewBinding
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
import com.denchic45.widget.extendedAdapter.ListItemAdapterDelegate

class SpecialtyAdapterDelegate :
    ListItemAdapterDelegate<SpecialtyResponse, SpecialtyAdapterDelegate.SpecialtyHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): SpecialtyHolder {
        return SpecialtyHolder(
            parent.viewBinding(ItemIconContentBinding::inflate)
        )
    }

    override fun isForViewType(item: Any): Boolean = item is SpecialtyResponse

    override fun onBindViewHolder(item: SpecialtyResponse, holder: SpecialtyHolder) {
        return holder.onBind(item)
    }

    class SpecialtyHolder(
        itemIconContentBinding: ItemIconContentBinding
    ) : BaseViewHolder<SpecialtyResponse, ItemIconContentBinding>(
            itemIconContentBinding
        ) {
        private val tvName: TextView = itemView.findViewById(R.id.tv_name)
        private val ivIcon: ImageView = itemView.findViewById(R.id.iv_icon)
        override fun onBind(item: SpecialtyResponse) {
            tvName.text = item.name
        }

        init {
            ivIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    itemView.context,
                    R.drawable.ic_specialty
                )
            )
        }
    }
}