package com.denchic45.studiversity.ui.adapter

import android.view.ViewGroup
import com.denchic45.studiversity.ui.model.Header
import com.denchic45.studiversity.databinding.ItemHeaderBinding
import com.denchic45.studiversity.util.viewBinding
import com.denchic45.studiversity.widget.extendedAdapter.ListItemAdapterDelegate

class HeaderAdapterDelegate :
    ListItemAdapterDelegate<Header, HeaderAdapterDelegate.HeaderHolder>() {

    override fun isForViewType(item: Any): Boolean {
        return item is Header
    }

    override fun onBindViewHolder(item: Header, holder: HeaderHolder) {
        holder.onBind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup): HeaderHolder {
        return HeaderHolder(parent.viewBinding(ItemHeaderBinding::inflate))
    }

    class HeaderHolder(itemHeaderBinding: ItemHeaderBinding) :
        BaseViewHolder<Header, ItemHeaderBinding>(itemHeaderBinding) {

        override fun onBind(item: Header) {
            with(binding) {
                tvHeader.text = item.title
            }
        }
    }
}