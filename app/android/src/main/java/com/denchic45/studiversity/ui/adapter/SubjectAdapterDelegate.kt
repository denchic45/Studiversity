package com.denchic45.studiversity.ui.adapter

import android.graphics.drawable.PictureDrawable
import android.view.ViewGroup
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.denchic45.studiversity.R
import com.denchic45.studiversity.SvgColorListener
import com.denchic45.studiversity.databinding.ItemIconContentBinding
import com.denchic45.studiversity.glideSvg.GlideApp
import com.denchic45.studiversity.util.viewBinding
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.denchic45.studiversity.widget.extendedAdapter.ListItemAdapterDelegate

class SubjectAdapterDelegate :
    ListItemAdapterDelegate<SubjectResponse, SubjectAdapterDelegate.SubjectHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): SubjectHolder {
        return SubjectHolder(
            parent.viewBinding(ItemIconContentBinding::inflate)
        )
    }

    override fun isForViewType(item: Any): Boolean = item is SubjectResponse

    override fun onBindViewHolder(item: SubjectResponse, holder: SubjectHolder) {
        return holder.onBind(item)
    }

    class SubjectHolder(
        itemIconContentBinding: ItemIconContentBinding
    ) : BaseViewHolder<SubjectResponse, ItemIconContentBinding>(itemIconContentBinding) {

        override fun onBind(item: SubjectResponse) {
            with(binding) {
                tvName.text = item.name

                com.denchic45.studiversity.glideSvg.GlideApp.with(itemView.context)
                    .`as`(PictureDrawable::class.java)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .listener(
                        SvgColorListener(
                            ivIcon,
                            R.color.dark_blue,
                            itemView.context
                        )
                    )
                    .load(item.iconUrl)
                    .into(ivIcon)
            }
        }
    }
}